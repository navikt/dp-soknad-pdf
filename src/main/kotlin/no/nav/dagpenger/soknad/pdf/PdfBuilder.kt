package no.nav.dagpenger.soknad.pdf

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.ByteArrayOutputStream

internal object PdfBuilder {

    internal fun lagPdf(html: String): ByteArray {
        return ByteArrayOutputStream().use {
            PdfRendererBuilder()
                .usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_U)
                .usePdfUaAccessbility(true)
                .withHtmlContent(html, null)
                .toStream(it)
                .run()
            it.toByteArray()
        }
    }
}
