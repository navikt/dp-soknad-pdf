package no.nav.dagpenger.innsending.pdf

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import java.io.ByteArrayOutputStream

internal object PdfBuilder {

    internal fun lagPdf(html: String): ByteArray {
        return ByteArrayOutputStream().use {
            PdfRendererBuilder()
                .usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_U)
                .usePdfUaAccessbility(true)
                .useColorProfile("/sRGB2014.icc".fileAsByteArray())
                .defaultTextDirection(BaseRendererBuilder.TextDirection.LTR)
                .withHtmlContent(html, null)
                .toStream(it)
                .run()
            it.toByteArray()
        }
    }
}
