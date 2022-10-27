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
import kotlinx.html.title

internal object HtmlBuilder {
    fun lagNettoHtml(innsending: Innsending) = lagHtml(innsending, DIV::nettoSeksjon, false)
    fun lagBruttoHtml(innsending: Innsending) = lagHtml(innsending, DIV::bruttoSeksjon, true)

    fun lagEttersendingHtml(innsending: Innsending): String {

        return lagHtml(innsending, { _, _ -> }, false)
    }

    private fun lagHtml(
        innsending: Innsending,
        seksjonFunksjon: DIV.(Innsending.Seksjon, Innsending.GenerellTekst) -> Unit = DIV::nettoSeksjon,
        brutto: Boolean
    ): String {
        val generellTekst = innsending.generellTekst
        return createHTMLDocument().html {
            attributes["xmlns"] = "http://www.w3.org/1999/xhtml"
            lang = innsending.språk.langAtributt
            head {
                title(innsending.generellTekst.tittel)
                pdfaMetaTags(innsending)
                fontimports()
                bookmarks(innsending)
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
                    div(classes = "dokumentasjon") {
                        id = "Dokumentasjon"
                        h2 {
                            +"Dokumentasjon"
                        }
                        dokumentasjonKrav(innsending.dokumentasjonskrav, Innsending.DokumentKrav.Valg.SEND_NAA, brutto)
                        dokumentasjonKrav(innsending.dokumentasjonskrav, Innsending.DokumentKrav.Valg.SEND_SENERE, brutto)
                        dokumentasjonKrav(innsending.dokumentasjonskrav, Innsending.DokumentKrav.Valg.SENDER_IKKE, brutto)
                        dokumentasjonKrav(innsending.dokumentasjonskrav, Innsending.DokumentKrav.Valg.SENDT_TIDLIGERE, brutto)
                        dokumentasjonKrav(innsending.dokumentasjonskrav, Innsending.DokumentKrav.Valg.ANDRE_SENDER, brutto)
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}
