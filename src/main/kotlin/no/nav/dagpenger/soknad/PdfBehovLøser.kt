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
import java.util.UUID

internal class PdfBehovLøser(
    rapidsConnection: RapidsConnection,
    private val pdfBuilder: PdfBuilder,
    private val pdfLagring: PdfLagring,
    private val soknadSupplier: suspend (soknadId: UUID) -> HtmlModell,
    private val htmlBuilder: (modell: HtmlModell) -> String = HtmlBuilder::lagHtml
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
            validate { it.requireKey("søknad_uuid", "ident") }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val soknadId = packet.søknadUuid()
        logg.info("Mottok behov for søknadspdf med uuid $soknadId")
        runBlocking {
            soknadSupplier(soknadId).let(htmlBuilder).let { pdfBuilder.lagPdf(it) }.let { pdf ->
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

private fun JsonMessage.søknadUuid(): UUID = this["søknad_uuid"].asText().let { UUID.fromString(it) }
