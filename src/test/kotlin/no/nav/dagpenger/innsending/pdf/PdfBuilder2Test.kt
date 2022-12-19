package no.nav.dagpenger.innsending.pdf

import no.nav.dagpenger.innsending.html.HtmlBuilder2
import no.nav.dagpenger.innsending.html.HtmlInliner
import no.nav.dagpenger.innsending.html.HtmlParser
import no.nav.dagpenger.innsending.html.TestHtml
import org.junit.jupiter.api.Test
import org.verapdf.gf.foundry.VeraGreenfieldFoundryProvider
import org.verapdf.pdfa.Foundries
import org.verapdf.pdfa.flavours.PDFAFlavour
import org.verapdf.pdfa.results.TestAssertion
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.test.assertTrue

internal class PdfBuilder2Test {
    companion object {
        private val resourceRetriever = object {}.javaClass
        private fun verifyPdf(pdf: ByteArray) {
            VeraGreenfieldFoundryProvider.initialise()
            Foundries.defaultInstance().use { foundry ->
                ByteArrayInputStream(pdf).use { pdf ->
                    val validator = foundry.createValidator(PDFAFlavour.PDFA_2_U, true)
                    foundry.createParser(pdf, PDFAFlavour.PDFA_2_U).also { parser ->
                        val result = validator.validate(parser).testAssertions.filter {
                            it.status == TestAssertion.Status.FAILED
                        }.distinctBy { it.ruleId }
                        assertTrue(
                            result.isEmpty(),
                            "PDF-A verifisering feiler på :\n ${result.map { it.ruleId }}, se https://docs.verapdf.org/validation/pdfa-parts-2-and-3/"
                        )
                    }
                }
            }
        }
    }

    @Test
    fun e2e() {
        val parsedHtml = HtmlParser.parse(TestHtml.simpleHtml)
        PdfBuilder2.lagPdf(
            HtmlBuilder2.build(parsedHtml).also {
                File("build/tmp/test/htmlbuilder2.html").writeText(it)
            }
        ).let {
            File("build/tmp/test/pdfbuilder2.pdf").writeBytes(it)
            verifyPdf(it)
        }
    }

    @Test
    fun `Hubba bubba`() {
        val inliner = HtmlInliner("https://arbeid.dev.nav.no")

        val html = inliner.inlineHtml(resourceRetriever.getResource("/hubba.html")!!.readText()).also {
            File("build/tmp/test/intline.html").writeText(it)
        }
        val lagPdf = PdfBuilder2.lagPdf(html).also {
            File("build/tmp/test/hubba_bubba.pdf").writeBytes(it)
        }

        VeraGreenfieldFoundryProvider.initialise()
        Foundries.defaultInstance().use { foundry ->
            val pdf = ByteArrayInputStream(lagPdf)
            val validator = foundry.createValidator(PDFAFlavour.PDFA_2_U, true)
            foundry.createParser(pdf, PDFAFlavour.PDFA_2_U).also { parser ->
                val result = validator.validate(parser).testAssertions.filter {
                    it.status == TestAssertion.Status.FAILED
                }.distinctBy { it.ruleId }
                assertTrue(
                    result.isEmpty(),
                    "PDF-A verifisering feiler på :\n ${result.map { it.ruleId }}, se https://docs.verapdf.org/validation/pdfa-parts-2-and-3/"
                )
            }
        }
    }
}
