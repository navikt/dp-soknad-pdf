package no.nav.dagpenger.soknad.pdf

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dagpenger.soknad.ArkiverbartDokument
import no.nav.dagpenger.soknad.PdfBehovLøser
import no.nav.dagpenger.soknad.html.TestModellHtml.htmlModell
import no.nav.dagpenger.soknad.leggTilUrn
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.test.assertEquals

internal class PdfBehovLøserTest {
    val soknadId = UUID.randomUUID()

    val testRapid = TestRapid().also {
        PdfBehovLøser(
            rapidsConnection = it,
            pdfBuilder = PdfBuilder,
            pdfLagring = mockk<PdfLagring>().also {
                coEvery {
                    it.lagrePdf(
                        soknadId.toString(),
                        any()
                    )
                } returns listOf(
                    ArkiverbartDokument.netto("<!DOCTYPE html>"),
                    ArkiverbartDokument.brutto("<!DOCTYPE html>")
                ).leggTilUrn(
                    listOf(
                        URNResponse("brutto.pdf", "urn:vedlegg:soknadId/brutto.pdf"),
                        URNResponse("netto.pdf", "urn:vedlegg:soknadId/netto.pdf")
                    )
                )
            },
            soknadSupplier = { _, _ -> htmlModell },
        )
    }

    @Test
    fun `besvarer pdf behov`() {
        testRapid.sendTestMessage(testMessage)
        assertEquals(1, testRapid.inspektør.size)
        @Language("JSON")
        val expectedLøsning = """
           [
                  {
                    "metainfo": {
                      "innhold": "netto.pdf",
                      "filtype": "PDF", 
                      "variant": "NETTO"
                    },
                    "urn": "urn:vedlegg:soknadId/netto.pdf"
                  },
                  {
                    "metainfo": {
                      "innhold": "brutto.pdf",
                      "filtype": "PDF",
                      "variant": "BRUTTO"
                    },
                    "urn": "urn:vedlegg:soknadId/brutto.pdf"
                  }
                ]
        """.trimIndent()

        assertJsonEquals(
            expectedLøsning,
            testRapid.inspektør.message(0)["@løsning"][PdfBehovLøser.BEHOV]
        )
    }

    private fun assertJsonEquals(expected: String, actual: JsonNode) {
        val objectMapper = jacksonObjectMapper()
        assertEquals(objectMapper.readTree(expected), actual)
    }

    @Test
    fun `besvarer ikke behov hvis løsning er besvart`() {
        testRapid.sendTestMessage(testMessageMedLøsning)
        assertEquals(0, testRapid.inspektør.size)
    }

    @Language("JSON")
    val testMessage = """ {
        "@event_name": "behov",
        "@behov": ["ArkiverbarSøknad"],
        "søknad_uuid": "$soknadId",
        "ident": "12345678910",
        "innsendtTidspunkt": "${ZonedDateTime.now(ZoneId.of("Europe/Oslo"))}"
            }
    """.trimIndent()

    @Language("JSON")
    val testMessageMedLøsning = """ {
        "@event_name": "behov",
        "@behov": ["ArkiverbarSøknad"],
        "@løsning": "something",
        "søknad_uuid": "$soknadId",
        "ident": "12345678910",
        "innsendtTidspunkt": "${ZonedDateTime.now(ZoneId.of("Europe/Oslo"))}}"
    """.trimIndent()
}
