package no.nav.dagpenger.innsending.html

import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.stream.createHTML
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

internal class HtmlParser(
    html: String,
    baseUri: String

) {
    private val document = Jsoup.parse(html, baseUri).also {
        it.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
    }

    fun parse(): ParsedHtml {
        return ParsedHtml(
            lang = getLang(),
            meta = getMeta(),
            title = document.title(),
            cssLinks = cssLinks(),
            body = document.body().outerHtml()
        )
    }

    private fun getLang(): String {
        return document.root().select("html")
            .takeIf { it.hasAttr("lang") }
            ?.attr("lang") ?: "nb"
    }

    private fun getMeta(): List<String> = document.head().select("meta").map { it.outerHtml() }

    private fun cssLinks(): List<HtmlString> {
        return document.head().select("link")
            .filter { element -> element.hasAttr("rel") && element.attr("rel") == "stylesheet" }
            .filter { element -> element.hasAttr("href") && element.attr("href").endsWith(".css") }
            .map { element ->
                val hrefAttr = element.attr("href")
                if (!hrefAttr.startsWith("http")) {
                    element.attr("href", element.absUrl("href"))
                }
                element
            }
            .map { it.outerHtml() }
    }
}

typealias HtmlString = String

internal data class ParsedHtml(
    val lang: String,
    val meta: List<HtmlString>,
    val title: String,
    val cssLinks: List<HtmlString>,
    val body: HtmlString
)

internal object HtmlBuilder2 {
    val tvungenFontStyle = """
        * {
            font-family: "Source Sans Pro" !important;
        }
        #__next {
            margin: 0cm 0.7cm;
            height: 850px;
        } 
    """.trimIndent()

    fun build(parsedHtml: ParsedHtml): String {
        return createHTML(
            prettyPrint = false,
            xhtmlCompatible = true
        ).html {
            lang = parsedHtml.lang
            head {
                title(parsedHtml.title)
                parsedHtml.meta.forEach { meta ->
                    unsafe { raw(meta) }
                }
                parsedHtml.cssLinks.forEach { meta ->
                    unsafe { raw(meta) }
                }
                style {
                    unsafe { raw(tvungenFontStyle) }
                }
            }
            this.body {
                unsafe { raw(parsedHtml.body) }
            }
        }
    }
}
