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

private val html5CharEntities = listOf(
    "&nbsp;" to "&#160;",
    "&iexcl;" to "&#161;",
    "&cent;" to "&#162;",
    "&pound;" to "&#163;",
    "&curren;" to "&#164;",
    "&yen;" to "&#165;",
    "&brvbar;" to "&#166;",
    "&sect;" to "&#167;",
    "&uml;" to "&#168;",
    "&copy;" to "&#169;",
    "&ordf;" to "&#170;",
    "&laquo;" to "&#171;",
    "&not;" to "&#172;",
    "&shy;" to "&#173;",
    "&reg;" to "&#174;",
    "&macr;" to "&#175;",
    "&deg;" to "&#176;",
    "&plusmn;" to "&#177;",
    "&sup2;" to "&#178;",
    "&sup3;" to "&#179;",
    "&acute;" to "&#180;",
    "&micro;" to "&#181;",
    "&para;" to "&#182;",
    "&middot;" to "&#183;",
    "&cedil;" to "&#184;",
    "&sup1;" to "&#185;",
    "&ordm;" to "&#186;",
    "&raquo;" to "&#187;",
    "&frac14;" to "&#188;",
    "&frac12;" to "&#189;",
    "&frac34;" to "&#190;",
    "&iquest;" to "&#191;",
    "&Agrave;" to "&#192;",
    "&Aacute;" to "&#193;",
    "&Acirc;" to "&#194;",
    "&Atilde;" to "&#195;",
    "&Auml;" to "&#196;",
    "&Aring;" to "&#197;",
    "&AElig;" to "&#198;",
    "&Ccedil;" to "&#199;",
    "&Egrave;" to "&#200;",
    "&Eacute;" to "&#201;",
    "&Ecirc;" to "&#202;",
    "&Euml;" to "&#203;",
    "&Igrave;" to "&#204;",
    "&Iacute;" to "&#205;",
    "&Icirc;" to "&#206;",
    "&Iuml;" to "&#207;",
    "&ETH;" to "&#208;",
    "&Ntilde;" to "&#209;",
    "&Ograve;" to "&#210;",
    "&Oacute;" to "&#211;",
    "&Ocirc;" to "&#212;",
    "&Otilde;" to "&#213;",
    "&Ouml;" to "&#214;",
    "&times;" to "&#215;",
    "&Oslash;" to "&#216;",
    "&Ugrave;" to "&#217;",
    "&Uacute;" to "&#218;",
    "&Ucirc;" to "&#219;",
    "&Uuml;" to "&#220;",
    "&Yacute;" to "&#221;",
    "&THORN;" to "&#222;",
    "&szlig;" to "&#223;",
    "&agrave;" to "&#224;",
    "&aacute;" to "&#225;",
    "&acirc;" to "&#226;",
    "&atilde;" to "&#227;",
    "&auml;" to "&#228;",
    "aring" to "&#229;",
    "&aelig;" to "&#230;",
    "&ccedil;" to "&#231;",
    "&egrave;" to "&#232;",
    "&eacute;" to "&#233;",
    "&ecirc;" to "&#234;",
    "&euml;" to "&#235;",
    "&igrave;" to "&#236;",
    "&iacute;" to "&#237;",
    "&icirc;" to "&#238;",
    "&iuml;" to "&#239;",
    "&eth;" to "&#240;",
    "&ntilde;" to "&#241;",
    "&ograve;" to "&#242;",
    "&oacute;" to "&#243;",
    "&ocirc;" to "&#244;",
    "&otilde;" to "&#245;",
    "&ouml;" to "&#246;",
    "&divide;" to "&#247;",
    "&oslash;" to "&#248;",
    "&ugrave;" to "&#249;",
    "&uacute;" to "&#250;",
    "&ucirc;" to "&#251;",
    "&uuml;" to "&#252;",
    "&yacute;" to "&#253;",
    "&thorn;" to "&#254;",
    "&yuml;" to "&#255;"
)

internal fun String.xhtmlCompliant() = this
    .replace(html5CharEntities)
    .replace(
        Regex("(<(meta|link).*?)>", RegexOption.IGNORE_CASE),
        replacement = "$1></$2>"
    )

fun String.replace(pairs: List<Pair<String, String>>): String =
    pairs.fold(this) { acc, (old, new) -> acc.replace(old, new, ignoreCase = true) }

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

private fun DIV.svar(språk: SøknadSpråk, svar: InnsendtSøknad.Svar, brutto: Boolean = false) {
    when (svar) {
        is InnsendtSøknad.EnkeltSvar -> boldSpanP(språk.svar, svar.tekst)
        is InnsendtSøknad.FlerSvar -> flersvar(svar, brutto)
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
        svar(språk, spmSvar.svar)
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
        svar(språk, spmSvar.svar, true)
        spmSvar.oppfølgingspørmål.forEach { oppfølging ->
            oppfølging.spørsmålOgSvar.forEach {
                bruttoSpørsmål(it, språk)
            }
        }
    }
}

private fun seksjonId(overskrift: String) = "seksjon-${overskrift.replace(" ", "-").lowercase()}"
