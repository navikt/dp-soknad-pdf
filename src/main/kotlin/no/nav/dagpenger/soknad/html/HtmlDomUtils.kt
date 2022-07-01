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
import no.nav.dagpenger.soknad.html.InnsendtSøknad.DokumentSpråk
import no.nav.dagpenger.soknad.html.InnsendtSøknad.PdfAMetaTagger
import no.nav.dagpenger.soknad.html.InnsendtSøknad.SporsmalSvar
import org.apache.commons.text.translate.EntityArrays.HTML40_EXTENDED_UNESCAPE
import org.apache.commons.text.translate.EntityArrays.ISO8859_1_UNESCAPE

private val html5CharEntities by lazy {
    (ISO8859_1_UNESCAPE + HTML40_EXTENDED_UNESCAPE).entries.associate {
        it.key.toString() to it.value.toString()
    }
}

internal fun String.xhtmlCompliant() = this
    .replace(html5CharEntities)
    .replace(
        Regex("(<(meta|link).*?)>", RegexOption.IGNORE_CASE),
        replacement = "$1></$2>"
    )

fun String.replace(pairs: Map<String, String>) =
    pairs.entries.fold(this) { acc, (old, new) -> acc.replace(old, new) }

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

internal fun DIV.flersvar(svar: InnsendtSøknad.FlerSvar, brutto: Boolean) {
    if (svar.alternativ.isNotEmpty()) {
        ul {
            svar.alternativ.forEach {
                li { +it.tekst }
            }
        }
        if (brutto) {
            div {
                svar.alternativ.forEach { svaralternativ ->
                    svaralternativ.tilleggsinformasjon?.also { info ->
                        div(classes = "hjelpetekst") {
                            h3 { +tilleggsinformasjonOverskrift(info) }
                            p { +info.tekst }
                        }
                    }
                }
            }
        }
    }
}

private fun tilleggsinformasjonOverskrift(info: InnsendtSøknad.InfoTekst): String {
    var overskrift = info.type.name.lowercase()
    if (info.tittel != null) {
        overskrift += ": ${info.tittel}"
    }
    return overskrift
}

private fun DIV.svar(språk: DokumentSpråk, svar: InnsendtSøknad.Svar, brutto: Boolean = false) {
    when (svar) {
        is InnsendtSøknad.EnkeltSvar -> boldSpanP(språk.svar, svar.tekst)
        is InnsendtSøknad.FlerSvar -> flersvar(svar, brutto)
        InnsendtSøknad.IngenSvar -> {}
    }
}

internal fun DIV.nettoSeksjon(seksjon: InnsendtSøknad.Seksjon, språk: DokumentSpråk) {
    id = seksjonId(seksjon.overskrift)
    h2 { +seksjon.overskrift }
    seksjon.spmSvar.forEach { nettoSpørsmål(it, språk) }
}

private fun DIV.nettoSpørsmål(spmSvar: SporsmalSvar, språk: DokumentSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        svar(språk, spmSvar.svar)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                nettoSpørsmål(it, språk)
            }
        }
    }
}

internal fun DIV.bruttoSeksjon(seksjon: InnsendtSøknad.Seksjon, språk: DokumentSpråk) {
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

private fun DIV.bruttoSpørsmål(spmSvar: SporsmalSvar, språk: DokumentSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        spmSvar.beskrivelse?.also { p(classes = "infotekst") { +spmSvar.beskrivelse } }
        spmSvar.hjelpetekst?.also {
            div(classes = "hjelpetekst") {
                spmSvar.hjelpetekst.tittel?.also { tittel -> h3 { +tittel } }
                p { +spmSvar.hjelpetekst.tekst }
            }
        }
        svar(språk, spmSvar.svar, true)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                bruttoSpørsmål(it, språk)
            }
        }
    }
}

private fun seksjonId(overskrift: String) = "seksjon-${overskrift.replace(" ", "-").lowercase()}"
