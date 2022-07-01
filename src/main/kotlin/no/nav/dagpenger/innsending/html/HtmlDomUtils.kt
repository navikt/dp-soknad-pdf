package no.nav.dagpenger.innsending.html

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
import no.nav.dagpenger.innsending.html.Innsending.GenerellTekst
import no.nav.dagpenger.innsending.html.Innsending.SporsmalSvar
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

internal fun HEAD.pdfaMetaTags(innsending: Innsending) {
    with(innsending.pdfAMetaTagger) {
        meta {
            name = "description"
            content = description
        }
        meta {
            name = "subject"
            content = subject
        }

        meta {
            name = "author"
            content = author
        }
    }
}

internal fun HEAD.bookmarks(seksjoner: List<Innsending.Seksjon>) {
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

internal fun DIV.flersvar(svar: Innsending.FlerSvar, brutto: Boolean) {
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

private fun tilleggsinformasjonOverskrift(info: Innsending.InfoTekst): String {
    var overskrift = info.type.name.lowercase()
    if (info.tittel != null) {
        overskrift += ": ${info.tittel}"
    }
    return overskrift
}

private fun DIV.svar(tekst: GenerellTekst, svar: Innsending.Svar, brutto: Boolean = false) {
    when (svar) {
        is Innsending.EnkeltSvar -> boldSpanP(tekst.svar, svar.tekst)
        is Innsending.FlerSvar -> flersvar(svar, brutto)
        Innsending.IngenSvar -> {}
    }
}

internal fun DIV.nettoSeksjon(seksjon: Innsending.Seksjon, tekst: GenerellTekst) {
    id = seksjonId(seksjon.overskrift)
    h2 { +seksjon.overskrift }
    seksjon.spmSvar.forEach { nettoSpørsmål(it, tekst) }
}

private fun DIV.nettoSpørsmål(spmSvar: SporsmalSvar, tekst: GenerellTekst) {
    div {
        h3 { +spmSvar.sporsmal }
        svar(tekst, spmSvar.svar)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                nettoSpørsmål(it, tekst)
            }
        }
    }
}

internal fun DIV.bruttoSeksjon(seksjon: Innsending.Seksjon, tekst: GenerellTekst) {
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
        bruttoSpørsmål(it, tekst)
    }
}

private fun DIV.bruttoSpørsmål(spmSvar: SporsmalSvar, tekst: GenerellTekst) {
    div {
        h3 { +spmSvar.sporsmal }
        spmSvar.beskrivelse?.also { p(classes = "infotekst") { +spmSvar.beskrivelse } }
        spmSvar.hjelpetekst?.also {
            div(classes = "hjelpetekst") {
                spmSvar.hjelpetekst.tittel?.also { tittel -> h3 { +tittel } }
                p { +spmSvar.hjelpetekst.tekst }
            }
        }
        svar(tekst, spmSvar.svar, true)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                bruttoSpørsmål(it, tekst)
            }
        }
    }
}

private fun seksjonId(overskrift: String) = "seksjon-${overskrift.replace(" ", "-").lowercase()}"
