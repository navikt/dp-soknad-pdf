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
import no.nav.dagpenger.innsending.serder.Oppslag
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

internal fun HEAD.bookmarks(seksjoner: List<Innsending.Seksjon>, generellTekst: GenerellTekst) {
// TODO: Språktilpassning på statiske bokmerker
    val seksjonBokmerker = seksjoner.map {
        """<bookmark name = "${it.overskrift}" href="#${seksjonId(it.overskrift)}"></bookmark>"""
    }.joinToString("")

    unsafe {
        //language=HTML
        raw(
            """
                <bookmarks>
                    <bookmark name="${generellTekst.hovedOverskrift}" href="#hovedoverskrift"></bookmark>
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
                            info.unsafeHtmlBody?.let { unsafe { +info.unsafeHtmlBody.kode } }
                        }
                    }
                }
            }
        }
    }
}

internal fun DIV.dokumentasjonKrav(dokumentKrav: List<Innsending.DokumentKrav>, valg: Innsending.DokumentKrav.Valg) {

    when (valg) {
        Innsending.DokumentKrav.Valg.SEND_NAA -> {
            val innsendts = dokumentKrav.filterIsInstance<Innsending.Innsendt>()
            if (innsendts.isNotEmpty()) {
                p { +"Du har lagt ved følgende vedlegg: " }
                ul {
                    innsendts.forEach { dokumentKrav ->
                        li {
                            p { +(dokumentKrav.navn as Oppslag.TekstObjekt.FaktaTekstObjekt).text }
                        }
                    }
                }
            }
        }
        Innsending.DokumentKrav.Valg.SEND_SENERE -> {
            val innsendts = dokumentKrav.filterIsInstance<Innsending.IkkeInnsendtNå>().filter { it.valg == valg }
            dokumentKrav(innsendts, "Du har sagt at du skal sende følgende vedlegg:")
        }
        Innsending.DokumentKrav.Valg.SENDT_TIDLIGERE -> {
            val innsendts = dokumentKrav.filterIsInstance<Innsending.IkkeInnsendtNå>().filter { it.valg == valg }
            dokumentKrav(innsendts, "Du har sagt at du tidligere har sendt inn følgende vedlegg:")
        }
        Innsending.DokumentKrav.Valg.SENDER_IKKE -> {
            val innsendts = dokumentKrav.filterIsInstance<Innsending.IkkeInnsendtNå>().filter { it.valg == valg }
            dokumentKrav(innsendts, "Du har sagt at du ikke sender følgende vedlegg:")
        }
        Innsending.DokumentKrav.Valg.ANDRE_SENDER -> {
            val innsendts = dokumentKrav.filterIsInstance<Innsending.IkkeInnsendtNå>().filter { it.valg == valg }
            dokumentKrav(innsendts, "Du har sagt at andre skal sende følgende vedlegg:")
        }
    }
}

private fun DIV.dokumentKrav(innsendts: List<Innsending.IkkeInnsendtNå>, beskrivelse: String) {
    if (innsendts.isNotEmpty()) {
        p { +beskrivelse }
        ul {
            innsendts.forEach { dokumentKrav ->
                li {
                    p { +(dokumentKrav.navn as Oppslag.TekstObjekt.FaktaTekstObjekt).text }
                    p { +(dokumentKrav.begrunnelse) }
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
    try {
        seksjon.beskrivelse?.also { unsafe { +seksjon.beskrivelse.medCssKlasse("infotekst") } }
    } catch (error: Exception) {
        throw error
    }
    seksjon.hjelpetekst?.also {
        div(classes = "hjelpetekst") {
            seksjon.hjelpetekst.tittel?.also { tittel -> h3 { +tittel } }
            seksjon.hjelpetekst.unsafeHtmlBody?.let { unsafe { +seksjon.hjelpetekst.unsafeHtmlBody.kode } }
        }
    }
    seksjon.spmSvar.forEach {
        bruttoSpørsmål(it, tekst)
    }
}

private fun DIV.bruttoSpørsmål(spmSvar: SporsmalSvar, tekst: GenerellTekst) {
    div {
        h3 { +spmSvar.sporsmal }
        spmSvar.beskrivelse?.also { unsafe { +spmSvar.beskrivelse.medCssKlasse("infotekst") } }
        spmSvar.hjelpetekst?.also {
            div(classes = "hjelpetekst") {
                spmSvar.hjelpetekst.tittel?.also { tittel -> h3 { +tittel } }
                // unsafe { +spmSvar.hjelpetekst.unsafeHtmlBody.kode }
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
