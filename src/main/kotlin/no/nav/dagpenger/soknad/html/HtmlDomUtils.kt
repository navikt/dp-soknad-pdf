package no.nav.dagpenger.soknad.html

import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.unsafe
import no.nav.dagpenger.soknad.html.HtmlModell.PdfAKrav
import no.nav.dagpenger.soknad.html.HtmlModell.SporsmalSvar
import no.nav.dagpenger.soknad.html.HtmlModell.SøknadSpråk

internal fun String.xhtmlCompliant() = this
    .replace("&oslash;", "ø")
    .replace("&aring;", "å")
    .replace("&aelig;", "æ")
    .replace(
        Regex("(?<=<(meta|META)[a-zA-ZæøåÆØÅ=\\\"\\/\\s\\-\\.\\;0-9]{1,1000})>"),
        replacement = "/>"
    )
    .replace(
        Regex(pattern = "(?<=<(link[\\sa-zA-ZæøåÆØÅ=\\\"\\:\\/\\.0-9\\?\\+\\,@\\;\\&]{1,1000}))>"),
        replacement = "/>"
    )

internal fun HEAD.pdfaMetaTags(pdfAKrav: PdfAKrav) {
    meta {
        name = "description"
        content = pdfAKrav.description
    }
    meta {
        name = "subject"
        content = pdfAKrav.subject
    }

    meta {
        name = "author"
        content = pdfAKrav.author
    }
}

internal fun HEAD.bookmarks(seksjoner: List<HtmlModell.Seksjon>) {

    val seksjonBokmerker = seksjoner.map {
        """<bookmark name = "${it.overskrift}" href="#${seksjonId(it.overskrift)}"></bookmark>"""
    }.joinToString("")
    print(seksjonBokmerker)
    unsafe {
        //language=HTML
        raw(
            """
                <bookmarks>
                    <bookmark name="Søknad om dagpenger" href="#hovedoverskrift"></bookmark>
                    <bookmark name="Info om søknad" href="#infoblokk"></bookmark>
                    $seksjonBokmerker
                </bookmarks>
            """.trimIndent()
        )
    }
}

internal fun DIV.boldSpanP(boldTekst: String, vanligTekst: String) {
    p {
        span(classes = "boldSpan") { +"$boldTekst: " }
        +vanligTekst
    }
}

internal fun DIV.nettoSeksjon(seksjon: HtmlModell.Seksjon, språk: SøknadSpråk) {
    id = seksjonId(seksjon.overskrift)
    h2 { +seksjon.overskrift }
    seksjon.spmSvar.forEach { nettoSpørsmål(it, språk) }
}

private fun DIV.nettoSpørsmål(spmSvar: SporsmalSvar, språk: SøknadSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        boldSpanP(språk.svar, spmSvar.svar)
        spmSvar.oppfølgingspørmål?.forEach { oppfølging ->
            nettoSpørsmål(oppfølging, språk)
        }
    }
}

internal fun DIV.bruttoSeksjon(seksjon: HtmlModell.Seksjon, språk: SøknadSpråk) {
    id = seksjonId(seksjon.overskrift)
    h2 { +seksjon.overskrift }
    if (seksjon.description != null) {
        p(classes = "infotekst") {
            +seksjon.description
        }
    }
    if (seksjon.helpText != null) {
        p(classes = "hjelpetekst") {
            +seksjon.helpText
        }
    }
    seksjon.spmSvar.forEach { bruttoSpørsmål(it, språk) }
}

private fun DIV.bruttoSpørsmål(spmSvar: SporsmalSvar, språk: SøknadSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        if (spmSvar.infotekst != null) {
            p(classes = "infotekst") { +spmSvar.infotekst }
        }
        if (spmSvar.hjelpeTekst != null) {
            p(classes = "hjelpetekst") {
                +spmSvar.hjelpeTekst
            }
        }
        boldSpanP(språk.svar, spmSvar.svar)
        spmSvar.oppfølgingspørmål?.forEach { oppfølging ->
            bruttoSpørsmål(oppfølging, språk)
        }
    }
}

private fun seksjonId(overskrift: String) = "seksjon-${overskrift.replace(" ", "-").lowercase()}"
