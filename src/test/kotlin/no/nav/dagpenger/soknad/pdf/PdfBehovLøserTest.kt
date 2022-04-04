package no.nav.dagpenger.soknad.pdf

import io.mockk.coEvery
import io.mockk.mockk
import no.nav.dagpenger.mottak.tjenester.PdfBehovLøser
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class PdfBehovLøserTest {
    val testRapid = TestRapid().also {
        PdfBehovLøser(
            it, PdfBuilder(),
            mockk<PdfLagring>().also {
                coEvery { it.lagrePdf(any(), any()) } returns "urn:document:id/søknad.pdf"
            }
        )
    }

    @Test
    fun `besvarer pdf behov`() {
        testRapid.sendTestMessage(testMessage)
        assertEquals(1, testRapid.inspektør.size)
        assertEquals("urn:document:id/søknad.pdf", testRapid.inspektør.message(0)["@løsning"][PdfBehovLøser.BEHOV].asText())
    }

    @Test
    fun `besvarer ikke behov hvis løsning er besvart`() {
        testRapid.sendTestMessage(testMessageMedLøsning)
        assertEquals(0, testRapid.inspektør.size)
    }
}

@Language("JSON")
val testMessage = """ {
        "@behov": ["arkiverbarSøknad"],
        "søknad_uuid": "hasfakfhajkfhkasjfhk",
        "ident": "12345678910"
            }
""".trimIndent()

@Language("JSON")
val testMessageMedLøsning = """ {
        "@behov": ["arkiverbarSøknad"],
        "@løsning": "something",
        "søknad_uuid": "hasfakfhajkfhkasjfhk",
        "ident": "12345678910"
            }
""".trimIndent()
