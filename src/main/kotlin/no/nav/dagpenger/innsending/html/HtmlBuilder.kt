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
import kotlinx.html.p
import kotlinx.html.title
import no.nav.dagpenger.innsending.html.Innsending.InnsendingsSpråk.BOKMÅL

internal object HtmlBuilder {
    fun lagNettoHtml(innsending: Innsending) = lagHtml(innsending, DIV::nettoSeksjon, false)
    fun lagBruttoHtml(innsending: Innsending) = lagHtml(innsending, DIV::bruttoSeksjon, true)
    fun lagEttersendingHtml(innsending: Innsending) = lagHtml(innsending, { _, _ -> }, false)

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
                        dokumentasjonKrav(
                            innsending.dokumentasjonskrav,
                            Innsending.DokumentKrav.Valg.SEND_SENERE,
                            brutto
                        )
                        dokumentasjonKrav(
                            innsending.dokumentasjonskrav,
                            Innsending.DokumentKrav.Valg.SENDER_IKKE,
                            brutto
                        )
                        dokumentasjonKrav(
                            innsending.dokumentasjonskrav,
                            Innsending.DokumentKrav.Valg.SENDT_TIDLIGERE,
                            brutto
                        )
                        dokumentasjonKrav(
                            innsending.dokumentasjonskrav,
                            Innsending.DokumentKrav.Valg.ANDRE_SENDER,
                            brutto
                        )
                        // todo engelsk tekst. Hack
                        if (innsending.type == InnsendingSupplier.InnsendingType.DAGPENGER) {
                            if (innsending.språk == BOKMÅL) {
                                p { +"Frist for innsendinger er 14 dager etter at du sendte søknaden. Vi trenger dokumentasjonen for å vurdere om du har rett til dagpenger. Du er ansvarlig for at dokumentasjonen sendes til oss. Hvis du ikke sender alle dokumentene innen fristen kan du få avslag på søknaden, fordi NAV mangler viktige opplysninger i saken din. Ta kontakt hvis du ikke rekker å ettersende alle dokumentene." }
                            }
                        }
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}
