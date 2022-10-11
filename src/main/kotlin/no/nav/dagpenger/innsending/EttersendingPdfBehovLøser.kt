package no.nav.dagpenger.innsending

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.html.Innsending.InnsendingsSpråk.BOKMÅL
import no.nav.dagpenger.innsending.html.Innsending.InnsendingsSpråk.ENGELSK
import no.nav.dagpenger.innsending.pdf.PdfLagring
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.time.ZonedDateTime
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
                            ).also {
                                logg.info { "Svar fra dp-mellomlagring: $it" }
                                logg.info { "Mappet til packet: $it" }
                                packet["@løsning"] = mapOf(BEHOV to it.behovSvar())
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

private fun JsonMessage.dokumentSpråk(): Innsending.InnsendingsSpråk = when (this["dokument_språk"].asText()) {
    "en" -> ENGELSK
    "nb" -> BOKMÅL
    else -> BOKMÅL
}

private fun List<LagretDokument>.behovSvar(): List<BehovSvarEtter> = this.map {
    BehovSvarEtter(
        metainfo = BehovSvarEtter.MetaInfo(
            innhold = it.filnavn,
            variant = it.variant.name
        )
    )
}

internal data class BehovSvarEtter(val metainfo: MetaInfo) {
    data class MetaInfo(val innhold: String, val filtype: String = "PDF", val variant: String)
}

private fun JsonMessage.ident() = this["ident"].asText()
private fun JsonMessage.innsendtTidspunkt(): ZonedDateTime =
    this["innsendtTidspunkt"].asZonedDateTime()

private fun JsonMessage.søknadUuid(): UUID = this["søknad_uuid"].asText().let { UUID.fromString(it) }
private fun JsonNode.asZonedDateTime(): ZonedDateTime = asText().let { ZonedDateTime.parse(it) }
