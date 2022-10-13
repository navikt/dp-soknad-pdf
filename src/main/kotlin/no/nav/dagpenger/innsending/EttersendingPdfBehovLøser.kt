package no.nav.dagpenger.innsending

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.dagpenger.innsending.LagretDokument.Companion.behovSvar
import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.pdf.PdfLagring
import no.nav.dagpenger.innsending.serder.dokumentSpråk
import no.nav.dagpenger.innsending.serder.ident
import no.nav.dagpenger.innsending.serder.innsendtTidspunkt
import no.nav.dagpenger.innsending.serder.søknadUuid
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.util.UUID

internal class EttersendingPdfBehovLøser(
    rapidsConnection: RapidsConnection,
    private val pdfLagring: PdfLagring,
    private val innsendingSupplier: suspend (soknadId: UUID, innsendingsSpråk: Innsending.InnsendingsSpråk) -> Innsending
) : River.PacketListener {
    companion object {
        private val logg = KotlinLogging.logger {}
        const val BEHOV = "ArkiverbarSøknad"
    }

    init {
        River(rapidsConnection).apply {
            validate { it.demandValue("@event_name", "behov") }
            validate { it.demandAll("@behov", listOf(BEHOV)) }
            validate { it.rejectKey("@løsning") }
            validate { it.requireKey("søknad_uuid", "ident", "innsendtTidspunkt") }
            validate { it.requireValue("type", "ETTERSENDING") }
            validate { it.interestedIn("dokument_språk") }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val soknadId = packet.søknadUuid()
        val ident = packet.ident()
        withLoggingContext("søknadId" to soknadId.toString()) {
            try {
                logg.info("Mottok behov for PDF av ettersending")

                runBlocking {
                    innsendingSupplier(soknadId, packet.dokumentSpråk())
                        .apply {
                            infoBlokk =
                                Innsending.InfoBlokk(
                                    fødselsnummer = ident,
                                    innsendtTidspunkt = packet.innsendtTidspunkt()
                                )
                        }
                        .let { lagArkiverbarEttersending(it) }
                        .let { dokumenter ->
                            pdfLagring.lagrePdf(
                                søknadUUid = soknadId.toString(),
                                arkiverbartDokument = dokumenter,
                                fnr = ident
                            ).let {
                                with(it.behovSvar()) {
                                    logg.info { "@Løsning satt til $this" }
                                    packet["@løsning"] = mapOf(BEHOV to this)
                                }
                            }
                        }
                    context.publish(packet.toJson())
                }
            } catch (e: Exception) {
                logg.error(e) { "Kunne ikke lage PDF for søknad med id: $soknadId" }
                throw e
            }
        }
    }
}
