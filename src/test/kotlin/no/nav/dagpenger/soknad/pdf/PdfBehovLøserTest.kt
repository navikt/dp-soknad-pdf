package no.nav.dagpenger.soknad.pdf

import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dagpenger.soknad.html.TestModellHtml.htmlModell
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
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
                        any() // todo capture and equals on bytearray?
                    )
                } returns URNResponse("urn:document:id/søknad.pdf")
            },
            soknadSupplier = { _ -> htmlModell },
        )
    }

    @Test
    fun `besvarer pdf behov`() {
        testRapid.sendTestMessage(testMessage)
        assertEquals(1, testRapid.inspektør.size)

        assertEquals(
            "urn:document:id/søknad.pdf",
            testRapid.inspektør.message(0)["@løsning"][PdfBehovLøser.BEHOV].asText()
        )

//        assertEquals(expectedPdf, slot.captured)
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
        "ident": "12345678910"
            }
    """.trimIndent()

    @Language("JSON")
    val testMessageMedLøsning = """ {
        "@event_name": "behov",
        "@behov": ["ArkiverbarSøknad"],
        "@løsning": "something",
        "søknad_uuid": "$soknadId",
        "ident": "12345678910"
            }
    """.trimIndent()
}
