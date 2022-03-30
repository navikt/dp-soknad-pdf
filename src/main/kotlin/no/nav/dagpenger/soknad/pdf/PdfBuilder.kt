package no.nav.dagpenger.soknad.pdf

import java.io.FileNotFoundException
import java.io.InputStream

internal class PdfBuilder() {

    internal fun lagPdf(): ByteArray = "/søknad.pdf".fileAsByteArray()

    private fun String.fileAsInputStream(): InputStream {
        return object {}.javaClass.getResource(this)?.openStream()
            ?: throw FileNotFoundException()
    }

    private fun String.fileAsByteArray(): ByteArray = this.fileAsInputStream().use { it.readAllBytes() }
}
