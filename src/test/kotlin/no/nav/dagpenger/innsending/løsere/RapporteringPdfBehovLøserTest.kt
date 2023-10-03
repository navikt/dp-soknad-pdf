package no.nav.dagpenger.innsending.løsere

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dagpenger.innsending.ArkiverbartDokument.DokumentVariant.NETTO
import no.nav.dagpenger.innsending.LagretDokument
import no.nav.dagpenger.innsending.pdf.PdfLagring
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.test.assertEquals

internal class RapporteringPdfBehovLøserTest {
    val journalpostId = UUID.randomUUID()
    val testFnr = "12345678910"

    val testRapid = TestRapid().also {
        RapporteringPdfBehovLøser(
            rapidsConnection = it,
            pdfLagring = mockk<PdfLagring>().also {
                coEvery {
                    it.lagrePdf(
                        journalpostId.toString(),
                        any(),
                        testFnr,
                    )
                } returns listOf(
                    LagretDokument("urn:vedlegg:journalpostId/netto.pdf", NETTO, "netto.pdf"),
                )
            },
        )
    }

    @Test
    fun `besvarer pdf behov for rapportering`() {
        testRapid.sendTestMessage(testMessage)
        assertEquals(1, testRapid.inspektør.size)
        assertJsonEquals(
            expectedLøsning,
            testRapid.inspektør.message(0)["@løsning"][RapporteringPdfBehovLøser.BEHOV],
        )

        /*
        val array = it.toByteArray()
        val file = File("test.pdf")
        file.writeBytes(array)
        array
         */
    }

    @Test
    fun `besvarer ikke behov hvis løsning er besvart`() {
        testRapid.sendTestMessage(testMessageMedLøsning)
        assertEquals(0, testRapid.inspektør.size)
    }

    private fun assertJsonEquals(expected: String, actual: JsonNode) {
        val objectMapper = jacksonObjectMapper()
        assertEquals(objectMapper.readTree(expected), actual)
    }

    @Language("JSON")
    val expectedLøsning = """
           [
                  {
                    "metainfo": {
                      "innhold": "netto.pdf",
                      "filtype": "PDF", 
                      "variant": "NETTO"
                    },
                    "urn": "urn:vedlegg:journalpostId/netto.pdf"
                  }
                ]
    """.trimIndent()

    @Language("JSON")
    val json = """
        {
            "timestamp": "2023-09-28T09:00:15.396222",
            "claims": {
                "sub": "",
                "iss": ""
            },
            "image": "IMAGE",
            "kildekode": "COMMIT",
            "klient": "Ktor client",
            "språk": "no-NB",
            "rapportering": {
                "2023-09-25": {},
                "2023-09-26": {},
                "2023-09-27": {},
                "2023-09-28": {},
                "2023-09-29": {},
                "2023-09-30": {},
                "2023-10-01": {},
                "2023-10-02": {},
                "2023-10-03": {},
                "2023-10-04": {},
                "2023-10-05": {},
                "2023-10-06": {},
                "2023-10-07": {},
                "2023-10-08": {}
            }
        }
    """.trimIndent().replace("\"", "\\\"").replace("\n", "")

    val now = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))

    @Language("JSON")
    val testMessage = """
        {
            "@event_name": "behov",
            "@behov": ["OpprettPdfForRapportering"],
            "dokument_språk": "no-NB",
            "ident": "$testFnr",
            "periodeId": "$journalpostId",
            "journalpostId": "$journalpostId",
            "json": "$json",
            "skjemakode": "04-01.04",
            "type": "NY_DIALOG",
            "innsendtTidspunkt": "$now"
        }
    """.trimIndent()

    @Language("JSON")
    val testMessageMedLøsning = """ {
        "@event_name": "behov",
        "@behov": ["OpprettPdfForRapportering"],
        "@løsning": "something",
        "ident": "12345678910",
        "journalpostId": "$journalpostId",
        "innsendtTidspunkt": "$now"
    """.trimIndent()
}
