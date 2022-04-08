package no.nav.dagpenger.soknad.pdf

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.apache.pdfbox.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal class PdfBuilder {
    companion object {
        val fonts: List<FontMetadata> = listOf(
            FontMetadata(
                family = "Source Sans Pro",
                path = "SourceSansPro-Regular.ttf",
                weight = 400,
                style = BaseRendererBuilder.FontStyle.NORMAL,
                subset = false
            )
        )

        val colorProfile: ByteArray = IOUtils.toByteArray(this::class.java.getResourceAsStream("/sRGB2014.icc"))
    }

    internal fun lagPdf(): ByteArray = lagPdf("/søknad.html".fileAsString())

    internal fun lagPdf(html: String): ByteArray {
        return ByteArrayOutputStream().use {
            PdfRendererBuilder().apply {
                for (font in fonts) {
                    useFont({ ByteArrayInputStream(font.bytes) }, font.family, font.weight, font.style, font.subset)
                }
            }
                .usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_U)
                .usePdfUaAccessbility(true)
                .withHtmlContent(html, null)
                .toStream(it)
                .run()
            it.toByteArray()
        }
    }
}

internal data class FontMetadata(
    val family: String,
    val path: String,
    val weight: Int,
    val style: BaseRendererBuilder.FontStyle,
    val subset: Boolean
) {
    val bytes: ByteArray = ("/fonts/$path").fileAsByteArray()
}
