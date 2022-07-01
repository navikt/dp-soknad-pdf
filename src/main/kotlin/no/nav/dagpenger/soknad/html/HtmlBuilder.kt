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
    fun lagNettoHtml(innsendtSøknad: InnsendtSøknad) = lagHtml(innsendtSøknad, DIV::nettoSeksjon)
    fun lagBruttoHtml(innsendtSøknad: InnsendtSøknad) = lagHtml(innsendtSøknad, DIV::bruttoSeksjon)

    private fun lagHtml(
        innsendtSøknad: InnsendtSøknad,
        seksjonFunksjon: DIV.(InnsendtSøknad.Seksjon, InnsendtSøknad.GenerellTekst) -> Unit = DIV::nettoSeksjon
    ): String {
        val generellTekst = innsendtSøknad.generellTekst
        return createHTMLDocument().html {
            attributes["xmlns"] = "http://www.w3.org/1999/xhtml"
            lang = innsendtSøknad.språk.langAtributt
            head {
                title(innsendtSøknad.generellTekst.tittel)
                pdfaMetaTags()
                fontimports()
                bookmarks(innsendtSøknad.seksjoner)
                søknadPdfStyle()
            }
            body {
                h1 {
                    id = "hovedoverskrift"
                    +innsendtSøknad.generellTekst.hovedOverskrift
                }
                div(classes = "infoblokk") {
                    id = "infoblokk"
                    boldSpanP(boldTekst = generellTekst.fnr, vanligTekst = innsendtSøknad.infoBlokk.fødselsnummer)
                    boldSpanP(boldTekst = generellTekst.datoSendt, vanligTekst = innsendtSøknad.infoBlokk.datoSendt)
                }
                innsendtSøknad.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        seksjonFunksjon(seksjon, generellTekst)
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}
