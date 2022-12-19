package no.nav.dagpenger.innsending.html

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class HtmlParserTest {

    @Test
    fun `Hente ut lang attribute fra root`() {
        HtmlParser(TestHtml.simpleHtml, "https://arbeid.dev.nav.no").parse().lang shouldBe TestHtml.lang
    }

    @Test
    fun `Henter ut body`() {
        HtmlParser(TestHtml.simpleHtml, "https://arbeid.dev.nav.no").parse().let { parsedHtml ->
            parsedHtml.body.trimIndent() shouldBe """<body>
 <main></main>
 <p><hi>
   Hei på deg
  </hi></p>
</body>
            """.trimIndent()
        }
    }

    @Test
    fun `defaulter til nb hvis Lang attribute ikke finnes`() {
        HtmlParser("""<html></html>""".trimIndent(), "https://arbeid.dev.nav.no").parse().let { parsedHtml ->
            parsedHtml.lang shouldBe "nb"
        }
    }

    @Test
    fun `Henter ut meta`() {
        //language=HTML
        HtmlParser(TestHtml.simpleHtml, "https://arbeid.dev.nav.no").parse().let { parsedHtml ->
            parsedHtml.meta shouldBe listOf(
                """<meta name="subject" content="subject" />""",
                """<meta name="author" content="author" />""",
                """<meta name="description" content="description" />""",
            )
        }
    }

    @Test
    fun `Henter ut cssLinks`() {
        //language=HTML
        HtmlParser(TestHtml.simpleHtml, "https://soknad.dialog").parse().let { parsedHtml ->
            parsedHtml.cssLinks shouldBe listOf(
                """<link rel="stylesheet" href="https://soknad.dialog/dagpenger/dialog/_next/static/css/67aec5957859d7b0.css" data-n-g="" />""",
                """<link rel="stylesheet" href="https://soknad.dialog/dagpenger/dialog/_next/static/css/7017b59e0c86bc67.css" data-n-p="" />""",
                """<link rel="stylesheet" href="http://absolut.url/1.css" data-n-p="" />""",
                """<link rel="stylesheet" href="https://absolut.url/2.css" data-n-p="" />"""
            )
        }
    }
}
