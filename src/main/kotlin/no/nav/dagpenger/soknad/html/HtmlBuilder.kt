package no.nav.dagpenger.soknad.html

import kotlinx.html.DIV
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.title

internal object HtmlBuilder {
    fun lagNettoHtml(htmlModell: HtmlModell) = lagHtml(htmlModell, DIV::nettoSeksjon)
    fun lagBruttoHtml(htmlModell: HtmlModell) = lagHtml(htmlModell, DIV::bruttoSeksjon)

    private fun lagHtml(
        htmlModell: HtmlModell,
        seksjonFunksjon: DIV.(HtmlModell.Seksjon, HtmlModell.SøknadSpråk) -> Unit = DIV::nettoSeksjon
    ): String {
        val språk = htmlModell.metaInfo.språk
        return createHTMLDocument().html {
            lang = språk.langAtributt
            head {
                title(htmlModell.metaInfo.tittel)
                pdfaMetaTags(htmlModell.pdfAKrav)
                fontimports()
                bookmarks(htmlModell.seksjoner)
                søknadPdfStyle()
            }
            body {
                h1 {
                    id = "hovedoverskrift"
                    +htmlModell.metaInfo.hovedOverskrift
                }
                div(classes = "infoblokk") {
                    id = "infoblokk"
                    boldSpanP(boldTekst = språk.fødselsnummer, vanligTekst = htmlModell.infoBlokk.fødselsnummer)
                    boldSpanP(boldTekst = språk.datoSendt, vanligTekst = htmlModell.infoBlokk.datoSendt)
                }
                htmlModell.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        seksjonFunksjon(seksjon, språk)
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}
