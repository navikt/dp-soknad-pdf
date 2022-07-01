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
    fun lagNettoHtml(innsendtDokument: InnsendtDokument) = lagHtml(innsendtDokument, DIV::nettoSeksjon)
    fun lagBruttoHtml(innsendtDokument: InnsendtDokument) = lagHtml(innsendtDokument, DIV::bruttoSeksjon)

    private fun lagHtml(
        innsendtDokument: InnsendtDokument,
        seksjonFunksjon: DIV.(InnsendtDokument.Seksjon, InnsendtDokument.GenerellTekst) -> Unit = DIV::nettoSeksjon
    ): String {
        val generellTekst = innsendtDokument.generellTekst
        return createHTMLDocument().html {
            attributes["xmlns"] = "http://www.w3.org/1999/xhtml"
            lang = innsendtDokument.språk.langAtributt
            head {
                title(innsendtDokument.generellTekst.tittel)
                pdfaMetaTags()
                fontimports()
                bookmarks(innsendtDokument.seksjoner)
                søknadPdfStyle()
            }
            body {
                h1 {
                    id = "hovedoverskrift"
                    +innsendtDokument.generellTekst.hovedOverskrift
                }
                div(classes = "infoblokk") {
                    id = "infoblokk"
                    boldSpanP(boldTekst = generellTekst.fnr, vanligTekst = innsendtDokument.infoBlokk.fødselsnummer)
                    boldSpanP(boldTekst = generellTekst.datoSendt, vanligTekst = innsendtDokument.infoBlokk.datoSendt)
                }
                innsendtDokument.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        seksjonFunksjon(seksjon, generellTekst)
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}
