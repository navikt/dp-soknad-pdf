package no.nav.dagpenger.innsending.html

import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.stream.createHTML
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal object HtmlParser {

    fun parse(html: String): ParsedHtml {
        val document = Jsoup.parse(html)
        return ParsedHtml(
            lang = document.getLang(),
            meta = ParsedHtml.Meta(
                subject = document.getMeta("title", "hubbba"),
                description = document.getMeta("description", "hubbba"),
                author = document.getMeta("author", "hubbba")
            )
        )
    }

    fun Document.getLang(): String {
        return this.select("html")
            .takeIf { it.hasAttr("lang") }
            ?.attr("lang") ?: "nb"
    }

    fun Document.getMeta(name: String, defaultValue: String): String {
        val elements = select("meta").filter { element: Element -> element.attr("name") == name }
        if (elements.isEmpty() || !elements.single().hasAttr("content")) {
            return defaultValue
        }
        return elements.single().attr("content")
    }
}

internal data class ParsedHtml(
    val lang: String,
    val meta: Meta
) {
    data class Meta(
        val subject: String,
        val description: String,
        val author: String,
    )
}

internal object HtmlBuilder2 {
    fun build(parsedHtml: ParsedHtml): String {
        return createHTML(
            prettyPrint = false,
            xhtmlCompatible = true
        ).html {
            lang = parsedHtml.lang
            head {
            }
        }
    }
}
