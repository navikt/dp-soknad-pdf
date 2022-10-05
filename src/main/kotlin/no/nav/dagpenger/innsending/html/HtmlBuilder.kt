package no.nav.dagpenger.innsending.html

import kotlinx.html.DIV
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.lang
import kotlinx.html.li
import kotlinx.html.p
import kotlinx.html.title
import kotlinx.html.ul

internal object HtmlBuilder {
    fun lagNettoHtml(innsending: Innsending) = lagHtml(innsending, DIV::nettoSeksjon)
    fun lagBruttoHtml(innsending: Innsending) = lagHtml(innsending, DIV::bruttoSeksjon)

    private fun lagHtml(
        innsending: Innsending,
        seksjonFunksjon: DIV.(Innsending.Seksjon, Innsending.GenerellTekst) -> Unit = DIV::nettoSeksjon
    ): String {
        val generellTekst = innsending.generellTekst
        return createHTMLDocument().html {
            attributes["xmlns"] = "http://www.w3.org/1999/xhtml"
            lang = innsending.språk.langAtributt
            head {
                title(innsending.generellTekst.tittel)
                pdfaMetaTags(innsending)
                fontimports()
                bookmarks(innsending.seksjoner, innsending.generellTekst)
                søknadPdfStyle()
            }
            body {
                h1 {
                    id = "hovedoverskrift"
                    +innsending.generellTekst.hovedOverskrift
                }
                div(classes = "infoblokk") {
                    id = "infoblokk"
                    boldSpanP(boldTekst = generellTekst.fnr, vanligTekst = innsending.infoBlokk.fødselsnummer)
                    boldSpanP(boldTekst = generellTekst.datoSendt, vanligTekst = innsending.infoBlokk.datoSendt)
                }
                innsending.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        seksjonFunksjon(seksjon, generellTekst)
                    }
                }
                if (innsending.dokumentasjonskrav.isNotEmpty()) {
                    h2 {
                        +"Vedlegg"
                    }
                    p {
                        +"Du har lagt ved følgende vedlegg:"
                    }
                    ul {
                        li {
                            p {
                                +"Dokument"
                            }
                        }
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}
