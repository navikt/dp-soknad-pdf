package no.nav.dagpenger.soknad

import com.fasterxml.jackson.databind.JsonNode
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
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.reflect.KFunction1

internal class PdfBehovLøser(
    rapidsConnection: RapidsConnection,
    private val pdfBuilder: PdfBuilder,
    private val pdfLagring: PdfLagring,
    private val soknadSupplier: suspend (soknadId: UUID, ident: String) -> HtmlModell,
    private val htmlBuilder: KFunction1<HtmlModell, List<ArkiverbartDokument>> = HtmlBuilder::lagBruttoOgNettoHtml
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
                .apply {
                    forEach { dokument ->
                        dokument.pdfByteSteam = pdfBuilder.lagPdf(dokument)
                    }
                }
                .let { dokumenter ->
                    pdfLagring.lagrePdf(
                        søknadUUid = soknadId.toString(),
                        arkiverbartDokument = dokumenter
                    ).also {
                        logg.info { "Svar fra dp-mellomlagring: $it" }
                        logg.info { "Mappet til packet: $it" }
                        packet["@løsning"] = mapOf(BEHOV to it.behovSvar())
                    }
                }
            context.publish(packet.toJson())
        }
    }
}

private fun List<ArkiverbartDokument>.behovSvar(): List<BehovSvar> = this.map {
    BehovSvar(
        urn = it.urn,
        metainfo = BehovSvar.MetaInfo(
            innhold = it.filnavn,
            variant = it.variant.name
        )
    )
}

internal data class BehovSvar(val metainfo: BehovSvar.MetaInfo, val urn: String) {
    data class MetaInfo(val innhold: String, val filtype: String = "PDF", val variant: String)
}

private fun JsonMessage.ident() = this["ident"].asText()
private fun JsonMessage.innsendtTidspunkt(): LocalDateTime =
    this["innsendtTidspunkt"].asZonedDateTime().toLocalDateTime()

private fun JsonMessage.søknadUuid(): UUID = this["søknad_uuid"].asText().let { UUID.fromString(it) }
private fun JsonNode.asZonedDateTime(): ZonedDateTime = asText().let { ZonedDateTime.parse(it) }
