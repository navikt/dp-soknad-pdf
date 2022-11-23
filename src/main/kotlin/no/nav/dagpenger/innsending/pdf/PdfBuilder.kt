package no.nav.dagpenger.innsending.pdf

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import mu.KotlinLogging
import java.io.ByteArrayOutputStream

internal object PdfBuilder {
    private val sikkerlogg = KotlinLogging.logger("tjenestekall.PdfBuilder")

    internal fun lagPdf(html: String): ByteArray {
        return try {
            ByteArrayOutputStream().use {
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
        } catch (e: Exception) {
            sikkerlogg.error(e) { "Kunne ikke lage PDF av søknaden. HTML=$html" }
            throw e
        }
    }
}
