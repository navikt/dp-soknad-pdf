package no.nav.dagpenger.soknad.pdf

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException

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
                File("tadda.pdf").writeBytes(it)
            }
        }
    }
    private fun String.fileAsString(): String {
        return object {}.javaClass.getResource(this)?.openStream()?.buffered()?.reader()?.use {
            it.readText()
        } ?: throw FileNotFoundException()
    }
}
