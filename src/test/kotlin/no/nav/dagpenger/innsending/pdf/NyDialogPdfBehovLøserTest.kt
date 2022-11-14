package no.nav.dagpenger.innsending.pdf

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dagpenger.innsending.ArkiverbartDokument.DokumentVariant.BRUTTO
import no.nav.dagpenger.innsending.ArkiverbartDokument.DokumentVariant.NETTO
import no.nav.dagpenger.innsending.LagretDokument
import no.nav.dagpenger.innsending.NyDialogPdfBehovLøser
import no.nav.dagpenger.innsending.html.InnsendingSupplier
import no.nav.dagpenger.innsending.html.TestModellHtml.innsending
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.test.assertEquals

internal class NyDialogPdfBehovLøserTest {
    val soknadId = UUID.randomUUID()
    val testFnr = "12345678910"

    val mockInnsendingSupplier = mockk<InnsendingSupplier>().also {
        coEvery { it.hentSoknad(soknadId, any()) } returns innsending
    }

    val testRapid = TestRapid().also {
        NyDialogPdfBehovLøser(
            rapidsConnection = it,
            pdfLagring = mockk<PdfLagring>().also {
                coEvery {
                    it.lagrePdf(
                        soknadId.toString(),
                        any(),
                        testFnr
                    )
                } returns listOf(
                    LagretDokument("urn:vedlegg:soknadId/netto.pdf", NETTO, "netto.pdf"),
                    LagretDokument("urn:vedlegg:soknadId/brutto.pdf", BRUTTO, "brutto.pdf"),
                )
            },
            innsendingSupplier = mockInnsendingSupplier
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
            testRapid.inspektør.message(0)["@løsning"][NyDialogPdfBehovLøser.BEHOV]
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
        "type": "NY_DIALOG",
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
