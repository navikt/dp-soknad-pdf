package no.nav.dagpenger.soknad.pdf

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
internal class PdfBuilderTest {

    @Test
    fun `kan lage PDF`() {
        assertDoesNotThrow {
            val result = PdfBuilder().lagPdf()
            assertTrue(result.isNotEmpty())
        }
    }
}
