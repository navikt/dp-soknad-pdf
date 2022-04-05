package no.nav.dagpenger.mottak.tjenester

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.dagpenger.soknad.pdf.PdfBuilder
import no.nav.dagpenger.soknad.pdf.PdfLagring
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

internal class PdfBehovLøser(
    rapidsConnection: RapidsConnection,
    private val pdfBuilder: PdfBuilder,
    private val pdfLagring: PdfLagring
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
        logg.info("Mottok behov for søknadspdf med uuid ${packet.søknadUuid()}")
        /*
         2. Lagre pdfen (med dp-mellomlagring)
        3. Svare med en løsning med urn på behovet
        */

        runBlocking {
            pdfLagring.lagrePdf(
                søknadUUid = packet.søknadUuid(),
                pdf = pdfBuilder.lagPdf()
            ).also {
                packet["@løsning"] = mapOf(BEHOV to it.urn)
            }

            context.publish(packet.toJson())
        }
    }
}

private fun JsonMessage.søknadUuid(): String = this["søknad_uuid"].asText()
