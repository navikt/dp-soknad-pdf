package no.nav.dagpenger.innsending.html

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

class HtmlInliner(
    private val url: String,
) {

    fun inlineHtml(html: String): String {

        val doc = Jsoup.parse(html)
        doc.select("link").forEach {
            val rel = it.attr("rel")
            if (rel != "stylesheet") {
                it.remove()
                return@forEach
            }
            if (it.hasAttr("href")) {
                val href = it.attr("href")
                if (!href.endsWith(".css")) {
                    throw RuntimeException("Link med href som ikke er .css")
                }

                val newUrlString = if (href.startsWith("https")) {
                    href
                } else {
                    url + href
                }

                try {
                    val readText = URL(newUrlString).readText()
                    val stylesheet = readText.replace("@media print", "@media papirprint")
                    it.parent()?.append("<style>\n$stylesheet\n</style>")
                    it.remove()
                } catch (e: java.lang.Exception) {
                    println("hubba")
                }
            } else {
                throw RuntimeException("Link uten href")
            }
        }
        val tvungenFontStyle = """
    <style>
        * {
            font-family: "Source Sans Pro" !important;
        }
        #__next {
            margin: 0cm 0.7cm;
            height: 850px;
        }
    </style>            
        """
        doc.select("head").forEach {
            it.append(tvungenFontStyle)
        }
        doc.select("script").forEach {
            it.remove()
        }
        doc.select("meta").forEach {
            it.remove()
        }
        doc.select("svg").forEach {
            it.remove()
        }
        doc.select("img").forEach {
            it.remove()
        }
        val body = doc.selectFirst("body") ?: throw RuntimeException("Må ha html body")
        if (body.children().size != 1) {
            throw RuntimeException("Forventa bare en child til body")
        }
        val first = body.children().first()!!
        if (!first.hasAttr("id") || first.attr("id") != "__next") {
            throw RuntimeException("Forventa at første child har id __next")
        }
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)

        return doc.toString()
    }
}
