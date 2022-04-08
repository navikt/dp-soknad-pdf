package no.nav.dagpenger.soknad.pdf

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class PdfBuilderTest {

    @Test
    fun `kan lage PDF`() {
        assertDoesNotThrow {
            val result = PdfBuilder().lagPdf()
            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun `Kan lage PDF fra HTML`() {
        assertDoesNotThrow {
            PdfBuilder().lagPdf("/søknad.html".fileAsString()).also {
                File("build/tmp/test/søknad.pdf").writeBytes(it)
            }
        }
    }
}
