package no.nav.dagpenger.soknad.pdf

import no.nav.dagpenger.soknad.html.TestHtml.testHtml
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import java.io.File

internal class PdfBuilderTest {

    @Test
    fun `Kan lage PDF fra HTML`() {
        assertDoesNotThrow {
            PdfBuilder().lagPdf(testHtml).also {
                File("build/tmp/test/søknad.pdf").writeBytes(it)
            }
        }
    }
}
