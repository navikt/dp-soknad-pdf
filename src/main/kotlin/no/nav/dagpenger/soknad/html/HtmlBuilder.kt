package no.nav.dagpenger.soknad.html

import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.p
import kotlinx.html.style
import kotlinx.html.title
import no.nav.dagpenger.soknad.pdf.PdfBuilder
import java.io.File

internal object HtmlBuilder {
    fun lagHtml(htmlModell: HtmlModell): String {
        val språk = htmlModell.metaInfo.språk
        return createHTMLDocument().html {
            lang = språk.langAtributt
            head {
                title(htmlModell.metaInfo.tittel)
                pdfa(htmlModell.pdfAKrav)
                fontimports()
                style {
                    søknadPdfStyle()
                }
            }
            body {
                h1 {
                    +htmlModell.metaInfo.hovedOverskrift
                }
                div(classes = "infoblokk") {
                    boldSpanP(boldTekst= språk.fødselsnummer, vanligTekst=htmlModell.infoBlokk.fødselsnummer)
                    boldSpanP(boldTekst = språk.datoSendt, vanligTekst = htmlModell.infoBlokk.fødselsnummer)
                }
                htmlModell.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        h2 { +seksjon.overskrift }
                        seksjon.spmSvar.forEach { ss ->
                            h3 { +ss.sporsmal }
                            boldSpanP(htmlModell.metaInfo.språk.svar, ss.svar)
                        }
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}

fun main() {
    testHtml.also { generertHtml ->
        print(generertHtml)
        File("soknad.html").writeText(generertHtml)
        PdfBuilder().lagPdf(generertHtml).also {
            File("søknad.pdf").writeBytes(it)
        }
    }
}

val testHtml = HtmlBuilder.lagHtml(
    htmlModell = HtmlModell(
        seksjoner = listOf(
            HtmlModell.Seksjon(
                overskrift = "Reel arbeidsøker", spmSvar = listOf(
                    HtmlModell.SporsmalSvar(sporsmal = "Kan du jobbe både heltid og deltid?", svar = "Ja"),
                    HtmlModell.SporsmalSvar(sporsmal = "Kan du jobbe i hele Norge?", svar = "Ja"),
                    HtmlModell.SporsmalSvar(sporsmal = "Kan du ta alle typer arbeid?", svar = "Ja"),
                    HtmlModell.SporsmalSvar(
                        sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                        svar = "Ja"
                    ),
                )

            ), HtmlModell.Seksjon(
                overskrift = "Seksjon 2", spmSvar = listOf(
                    HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 1", svar = "svar 1"),
                    HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 2", svar = "svar 2"),
                    HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 3", svar = "svar 3"),
                )
            )
        ), metaInfo = HtmlModell.MetaInfo(
            hovedOverskrift = "Søknad om dagpenger"
        ),
        pdfAKrav = HtmlModell.PdfAKrav("Søknad om dagpenger"),
        infoBlokk = HtmlModell.InfoBlokk("12345678910", "24.03.2022 11.34")
    )
)