package no.nav.dagpenger.innsending.pdf

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dagpenger.innsending.ArkiverbartDokument.DokumentVariant.NETTO
import no.nav.dagpenger.innsending.EttersendingPdfBehovLøser
import no.nav.dagpenger.innsending.LagretDokument
import no.nav.dagpenger.innsending.html.TestModellHtml.innsending
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.test.assertEquals

internal class EttersendingPdfBehovLøserTest {
    val soknadId = UUID.randomUUID()
    val testFnr = "12345678910"

    val testRapid = TestRapid().also {
        EttersendingPdfBehovLøser(
            rapidsConnection = it,
            pdfLagring = mockk<PdfLagring>().also {
                coEvery {
                    it.lagrePdf(
                        soknadId.toString(),
                        any(),
                        testFnr
                    )
                } returns listOf(
                    LagretDokument("urn:vedlegg:soknadId/ettersending.pdf", NETTO, "ettersending.pdf"),
                )
            },
            innsendingSupplier = { _, _ -> innsending },
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
                      "innhold": "ettersending.pdf",
                      "filtype": "PDF", 
                      "variant": "NETTO"
                    },
                    "urn": "urn:vedlegg:soknadId/ettersending.pdf"
                  }
                ]
        """.trimIndent()

        assertJsonEquals(
            expectedLøsning,
            testRapid.inspektør.message(0)["@løsning"][EttersendingPdfBehovLøser.BEHOV]
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
        "dokument_språk": "en",
        "søknad_uuid": "$soknadId",
        "ident": "$testFnr",
        "type": "ETTERSENDING_TIL_DIALOG",
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
