package no.nav.dagpenger.soknad.pdf

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import no.nav.dagpenger.soknad.ArkiverbartDokument
import java.io.ByteArrayOutputStream

internal object PdfBuilder {

//    internal fun lagPdf(): ByteArray = lagPdf("/søknad.html".fileAsString())

    internal fun lagPdf(arkiverbartDokument: ArkiverbartDokument): ByteArray {
        return ByteArrayOutputStream().use {
            PdfRendererBuilder()
                .usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_U)
                .usePdfUaAccessbility(true)
                .useColorProfile("/sRGB2014.icc".fileAsByteArray())
                .defaultTextDirection(BaseRendererBuilder.TextDirection.LTR)
                .withHtmlContent(arkiverbartDokument.html, null)
                .toStream(it)
                .run()
            it.toByteArray()
        }
    }
}
