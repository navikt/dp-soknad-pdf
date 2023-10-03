package no.nav.dagpenger.innsending.løsere

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.html.BODY
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.dagpenger.innsending.ArkiverbartDokument
import no.nav.dagpenger.innsending.LagretDokument.Companion.behovSvar
import no.nav.dagpenger.innsending.html.søknadPdfStyle
import no.nav.dagpenger.innsending.pdf.PdfBuilder
import no.nav.dagpenger.innsending.pdf.PdfLagring
import no.nav.dagpenger.innsending.serder.ident
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.util.UUID

internal class RapporteringPdfBehovLøser(
    rapidsConnection: RapidsConnection,
    private val pdfLagring: PdfLagring,
) : River.PacketListener {
    companion object {
        private val logg = KotlinLogging.logger {}
        private val sikkerlogg = KotlinLogging.logger("tjenestekall")
        const val BEHOV = "OpprettPdfForRapportering"
    }

    init {
        River(rapidsConnection).apply {
            validate { it.demandValue("@event_name", "behov") }
            validate { it.demandAll("@behov", listOf(BEHOV)) }
            validate { it.rejectKey("@løsning") }
            validate { it.requireKey("ident", "periodeId", "journalpostId", "json") }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val ident = packet.ident()
        val periodeId = packet["periodeId"].asText().let { UUID.fromString(it) }
        val journalpostId = packet["journalpostId"].asText()
        val json = jacksonObjectMapper().readTree(packet["json"].asText())

        withLoggingContext(
            "periodeId" to periodeId.toString(),
            "journalpostId" to journalpostId,
        ) {
            try {
                runBlocking(MDCContext()) {
                    logg.info("Mottok behov for PDF av rapportering")

                    val html = createHTML(prettyPrint = false, xhtmlCompatible = false).html {
                        lang = json["språk"].asText()
                        head {
                            title("Rapporteringperiode $periodeId")
                            søknadPdfStyle()
                        }
                        body {
                            iterate(json, "") // pre-tagen fungerer ikke, derfor må vi gjøre formatering selv
                        }
                    }

                    pdfLagring.lagrePdf(
                        søknadUUid = journalpostId,
                        arkiverbartDokument = listOf(ArkiverbartDokument.netto(PdfBuilder.lagPdf(html))),
                        fnr = ident,
                    ).let {
                        with(it.behovSvar()) {
                            packet["@løsning"] = mapOf(BEHOV to this)
                        }
                    }

                    with(packet.toJson()) {
                        context.publish(this)
                        sikkerlogg.info { "Sender løsning for $BEHOV: $this" }
                    }
                }
            } catch (e: Exception) {
                logg.error(e) { "Kunne ikke lage PDF for periode med id: $periodeId" }
                throw e
            }
        }
    }

    private fun BODY.iterate(json: JsonNode, indent: String) {
        val iterator = json.fields()
        while (iterator.hasNext()) {
            val item = iterator.next()

            if (item.value.nodeType == JsonNodeType.OBJECT) {
                div { +"$indent ${item.key}: {" }
                iterate(item.value, "__")
                div { +"$indent }" }
            } else {
                div {
                    +"$indent ${item.key}: ${item.value.asText()}"
                }
            }
        }
    }
}
