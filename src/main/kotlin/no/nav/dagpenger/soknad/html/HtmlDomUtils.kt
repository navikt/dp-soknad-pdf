package no.nav.dagpenger.soknad.html

import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.html.h3
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.ul
import kotlinx.html.unsafe
import no.nav.dagpenger.soknad.html.InnsendtSøknad.PdfAMetaTagger
import no.nav.dagpenger.soknad.html.InnsendtSøknad.SporsmalSvar
import no.nav.dagpenger.soknad.html.InnsendtSøknad.SøknadSpråk

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

internal fun HEAD.pdfaMetaTags() {
    meta {
        name = "description"
        content = PdfAMetaTagger.description
    }
    meta {
        name = "subject"
        content = PdfAMetaTagger.subject
    }

    meta {
        name = "author"
        content = PdfAMetaTagger.author
    }
}

internal fun HEAD.bookmarks(seksjoner: List<InnsendtSøknad.Seksjon>) {
// TODO: Språktilpassning på statiske bokmerker
    val seksjonBokmerker = seksjoner.map {
        """<bookmark name = "${it.overskrift}" href="#${seksjonId(it.overskrift)}"></bookmark>"""
    }.joinToString("")

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

internal fun DIV.boldSpanP(boldTekst: String, svar: InnsendtSøknad.EnkeltSvar) {
    p {
        span(classes = "boldSpan") { +"$boldTekst: " }
        +svar.tekst
    }
}

internal fun DIV.boldSpanP(boldTekst: String, svar: InnsendtSøknad.FlerSvar) {
    if (svar.tekster.isNotEmpty()) {
        ul {
            svar.tekster.forEach {
                li { +it }
            }
        }
    }
}

internal fun DIV.boldSpanP(boldTekst: String, svar: InnsendtSøknad.Svar) {
    when (svar) {
        is InnsendtSøknad.EnkeltSvar -> boldSpanP(boldTekst, svar)
        is InnsendtSøknad.FlerSvar -> boldSpanP(boldTekst, svar)
        InnsendtSøknad.IngenSvar -> {}
    }
}

internal fun DIV.nettoSeksjon(seksjon: InnsendtSøknad.Seksjon, språk: SøknadSpråk) {
    id = seksjonId(seksjon.overskrift)
    h2 { +seksjon.overskrift }
    seksjon.spmSvar.forEach { nettoSpørsmål(it, språk) }
}

private fun DIV.nettoSpørsmål(spmSvar: SporsmalSvar, språk: SøknadSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        boldSpanP(språk.svar, spmSvar.svar)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                nettoSpørsmål(it, språk)
            }
        }
    }
}

internal fun DIV.bruttoSeksjon(seksjon: InnsendtSøknad.Seksjon, språk: SøknadSpråk) {
    id = seksjonId(seksjon.overskrift)
    h2 { +seksjon.overskrift }
    seksjon.beskrivelse?.also { p(classes = "infotekst") { +seksjon.beskrivelse } }
    seksjon.hjelpetekst?.also {
        div(classes = "hjelpetekst") {
            seksjon.hjelpetekst.tittel?.also { tittel -> h3 { +tittel } }
            p { +seksjon.hjelpetekst.tekst }
        }
    }
    seksjon.spmSvar.forEach {
        bruttoSpørsmål(it, språk)
    }
}

private fun DIV.bruttoSpørsmål(spmSvar: SporsmalSvar, språk: SøknadSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        spmSvar.beskrivelse?.also { p(classes = "infotekst") { +spmSvar.beskrivelse } }
        spmSvar.hjelpetekst?.also {
            div(classes = "hjelpetekst") {
                spmSvar.hjelpetekst.tittel?.also { tittel -> h3 { +tittel } }
                p { +spmSvar.hjelpetekst.tekst }
            }
        }
        boldSpanP(språk.svar, spmSvar.svar)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                bruttoSpørsmål(it, språk)
            }
        }
    }
}

private fun seksjonId(overskrift: String) = "seksjon-${overskrift.replace(" ", "-").lowercase()}"
