package no.nav.dagpenger.innsending.html

import io.kotest.matchers.shouldBe
import no.nav.dagpenger.innsending.html.HtmlParser.getLang
import no.nav.dagpenger.innsending.html.HtmlParser.getMeta
import org.jsoup.Jsoup
import org.junit.jupiter.api.Test

internal class HtmlParserTest {
    @Test
    fun `Hente ut lang attribute fra root`() {
        //language=HTML
        Jsoup.parse("""<html lang='nb'></html>""".trimIndent()).getLang() shouldBe "nb"
    }

    @Test
    fun `defaulter til nb hvis Lang attribute ikke finnes`() {
        //language=HTML
        Jsoup.parse("""<html></html>""".trimIndent()).getLang() shouldBe "nb"
    }

    @Test
    fun `Henter ut meta`() {
        //language=HTML
        val docomument = Jsoup.parse(
            """<html lang='nb'>
<head>
    <meta name="subject" content="subject"/>
    <meta name="author" content="author"/>
    <meta name="description" content="description"/>
</head>

            """.trimIndent()
        )

        docomument.getMeta("subject", "") shouldBe "subject"
        docomument.getMeta("author", "") shouldBe "author"
        docomument.getMeta("description", "") shouldBe "description"
    }

    @Test
    fun `Henter ut meta default verdier `() {
        //language=HTML
        val docomument = Jsoup.parse(
            """<html lang='nb'> <head> <meta name="subject" class="subject"/></head> </html> """.trimIndent()
        )

        docomument.getMeta("subject", "default subject") shouldBe "default subject"
        docomument.getMeta("author", "default author") shouldBe "default author"
        docomument.getMeta("description", "default description") shouldBe "default description"
    }
}
