package no.nav.dagpenger.soknad.html

import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.html
import kotlinx.html.p
import java.io.File

object HtmlBuilder {
    fun lagHtml(htmlModell: HtmlModell): String {
        return createHTMLDocument().html {
            body {
                htmlModell.seksjoner.forEach { seksjon ->
                    div {
                        h2 { +seksjon.overskrift }
                        seksjon.spmSvar.forEach { ss ->
                            h3 { +ss.sporsmal }
                            p { +"Svar: ${ss.svar}" }
                        }
                    }
                }
            }
        }.serialize(true)
    }
}

fun main() {
    HtmlBuilder.lagHtml(
        htmlModell = HtmlModell(
            seksjoner = listOf(
                HtmlModell.Seksjon(
                    overskrift = "Seksjon 1",
                    spmSvar = listOf(
                        HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 1", svar = "svar 1"),
                        HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 2", svar = "svar 2"),
                        HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 3", svar = "svar 3"),
                    )

                ),
                HtmlModell.Seksjon(
                    overskrift = "Seksjon 2",
                    spmSvar = listOf(
                        HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 1", svar = "svar 1"),
                        HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 2", svar = "svar 2"),
                        HtmlModell.SporsmalSvar(sporsmal = "Dett er spm 3", svar = "svar 3"),
                    )
                )
            )
        )
    ).also {
        File("soknad.html").writeText(it)
    }
}
