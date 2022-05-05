package no.nav.dagpenger.soknad.html

import kotlinx.html.HEAD
import kotlinx.html.STYLE
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
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe
import java.io.File

internal object HtmlBuilder {
    fun lagHtml(htmlModell: HtmlModell): String {
        return createHTMLDocument().html {
            lang = htmlModell.metaInfo.språk.langAtributt
            head {
                title(htmlModell.metaInfo.tittel)
                fontimports()
                style {
                    søknadPdfStyle()
                }
            }
            body {
                h1 {
                    +htmlModell.metaInfo.hovedOverskrift
                }
                htmlModell.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        h2 { +seksjon.overskrift }
                        seksjon.spmSvar.forEach { ss ->
                            h3 { +ss.sporsmal }
                            p { +"${htmlModell.metaInfo.språk.svar}: ${ss.svar}" }
                        }
                    }
                }
            }
        }.serialize(true)
    }
}

private fun STYLE.søknadPdfStyle() {
    unsafe {
        //language=CSS
        raw(
            """
                     body{
                         font-family: 'Source Sans Pro';
                         padding: 5px;
                         
                     }
                     .seksjon{
                     }

                            """.trimIndent()
        )
    }
}

private fun HEAD.fontimports() {
    link {
        rel = "preconnect"
        href = "https://fonts.googleapis.com"
    }
    link {
        rel = "preconnect"
        href = "https://fonts.gstatic.com"
    }
    link {
        href = "https://fonts.googleapis.com/css2?family=Source+Sans+Pro:ital,wght@0,300;0,400;0,600;1,400&display=swap"
        rel = "stylesheet"
    }
}

fun main() {
    HtmlBuilder.lagHtml(
        htmlModell = HtmlModell(
            seksjoner = listOf(
                HtmlModell.Seksjon(
                    overskrift = "Reel arbeidsøker", spmSvar = listOf(
                        HtmlModell.SporsmalSvar(sporsmal = "Kan du jobbe både heltid og deltid?", svar = "Ja"),
                        HtmlModell.SporsmalSvar(sporsmal = "Kan du jobbe i hele Norge?", svar = "Ja"),
                        HtmlModell.SporsmalSvar(sporsmal = "Kan du ta alle typer arbeid?", svar = "Ja"),
                        HtmlModell.SporsmalSvar(sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?", svar = "Ja"),
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
            )
        )
    ).also {
        File("soknad.html").writeText(it)
    }
}
