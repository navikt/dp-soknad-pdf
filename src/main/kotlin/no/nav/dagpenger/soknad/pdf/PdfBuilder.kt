package no.nav.dagpenger.soknad.pdf

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.apache.pdfbox.io.IOUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

internal class PdfBuilder() {
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

    internal fun lagPdf(): ByteArray = "/søknad.pdf".fileAsByteArray()

    private fun String.fileAsInputStream(): InputStream {
        return object {}.javaClass.getResource(this)?.openStream()
            ?: throw FileNotFoundException()
    }

    private fun String.fileAsByteArray(): ByteArray = this.fileAsInputStream().use { it.readAllBytes() }

    internal fun lagPdf(html: String): ByteArray {

        return ByteArrayOutputStream().use {
            PdfRendererBuilder().apply {
                for (font in fonts) {
                    useFont({ ByteArrayInputStream(font.bytes) }, font.family, font.weight, font.style, font.subset)
                }
            }
                .usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_2_U)
                .usePdfUaAccessbility(true)
                // .useSVGDrawer(BatikSVGDrawer())
                .withHtmlContent(html, null)
                .toStream(it)
                .run()
            it.toByteArray()
        }
        // require(verifyCompliance(pdf)) { "Non-compliant PDF/A :(" }
    }
}

data class FontMetadata(
    val family: String,
    val path: String,
    val weight: Int,
    val style: BaseRendererBuilder.FontStyle,
    val subset: Boolean
) {
    val bytes: ByteArray = FontMetadata::class.java.getResourceAsStream("/fonts/" + path).readAllBytes()
}

val congfigJson = """[
  {
    "family": "Source Sans Pro",
    "path": "SourceSansPro-Regular.ttf",
    "weight": 400,
    "style": "NORMAL",
    "subset": false
  },
  {
    "family": "Source Sans Pro",
    "path": "SourceSansPro-Bold.ttf",
    "weight": 700,
    "style": "NORMAL",
    "subset": false
  }
]"""
