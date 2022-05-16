package no.nav.dagpenger.soknad

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.dagpenger.soknad.html.HtmlBuilder
import no.nav.dagpenger.soknad.html.HtmlModell
import no.nav.dagpenger.soknad.pdf.PdfBuilder
import no.nav.dagpenger.soknad.pdf.PdfLagring
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.UUID

internal class PdfBehovLøser(
    rapidsConnection: RapidsConnection,
    private val pdfBuilder: PdfBuilder,
    private val pdfLagring: PdfLagring,
    private val soknadSupplier: suspend (soknadId: UUID, ident: String) -> HtmlModell,
    private val htmlBuilder: (modell: HtmlModell) -> String = HtmlBuilder::lagBruttoHtml
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
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        // TODO: brutto og netto urn-er
        val soknadId = packet.søknadUuid()
        val ident = packet.ident()
        logg.info("Mottok behov for søknadspdf med uuid $soknadId")
        runBlocking {
            soknadSupplier(soknadId, ident)
                .apply {
                    infoBlokk =
                        HtmlModell.InfoBlokk(fødselsnummer = ident, innsendtTidspunkt = packet.innsendtTidspunkt())
                }
                .let(htmlBuilder)
                .let { pdfBuilder.lagPdf(it) }
                .let { pdf ->
                    pdfLagring.lagrePdf(
                        søknadUUid = soknadId.toString(),
                        pdf = pdf
                    ).also {
                        packet["@løsning"] = mapOf(BEHOV to it.urn)
                    }
                }
            context.publish(packet.toJson())
        }
    }
}

private fun JsonMessage.ident() = this["ident"].asText()
private fun JsonMessage.innsendtTidspunkt(): LocalDateTime = this["innsendtTidspunkt"].asLocalDateTime()
private fun JsonMessage.søknadUuid(): UUID = this["søknad_uuid"].asText().let { UUID.fromString(it) }
