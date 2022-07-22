package no.nav.dagpenger.innsending.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.innsending.LandOppslag
import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.html.Innsending.EnkeltSvar
import no.nav.dagpenger.innsending.html.Innsending.Svar
import no.nav.helse.rapids_rivers.asLocalDate
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class JsonHtmlMapper(
    private val innsendingsData: String,
    tekst: String,
    private val språk: Innsending.InnsendingsSpråk = Innsending.InnsendingsSpråk.BOKMÅL,
) {
    private val oppslag = Oppslag(tekst)
    private val objectMapper = jacksonObjectMapper()

    private fun parse(innsendingsData: String): List<Innsending.Seksjon> {
        return objectMapper.readTree(innsendingsData)["seksjoner"].map {
            val tekstObjekt = oppslag.lookup(it["beskrivendeId"].asText()) as Oppslag.TekstObjekt.SeksjonTekstObjekt
            Innsending.Seksjon(
                overskrift = tekstObjekt.title,
                beskrivelse = tekstObjekt.description?.let { rawHtml -> Innsending.UnsafeHtml(rawHtml.html) },
                hjelpetekst = tekstObjekt.helpText(),
                spmSvar = it.fakta()
            )
        }
    }

    private fun JsonNode.svar(): Svar {
        return when (val type = this["type"].asText()) {
            "tekst" -> EnkeltSvar(this["svar"].asText())
            "double" -> EnkeltSvar(this["svar"].asText())
            "int" -> EnkeltSvar(this["svar"].asText())
            "boolean" -> EnkeltSvar((oppslag.lookup(this.booleanTextId()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt).text)
            "localdate" -> EnkeltSvar(this["svar"].asLocalDate().dagMånedÅr())
            "periode" -> EnkeltSvar(
                "${
                this["svar"]["fom"].asLocalDate().dagMånedÅr()
                } - ${this["svar"]["tom"].asLocalDate().dagMånedÅr()}"
            )
            "generator" -> Innsending.IngenSvar
            "envalg" -> EnkeltSvar((oppslag.lookup(this["svar"].asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt).text)
            "flervalg" -> Innsending.FlerSvar(this.flerValg())
            "land" -> EnkeltSvar(LandOppslag.hentLand(språk, this["svar"].asText()))
            "dokument" -> EnkeltSvar(this.dokumentTekst())
            else -> throw IllegalArgumentException("Ukjent faktumtype $type")
        }
    }

    private fun JsonNode.flerValg(): List<Innsending.SvarAlternativ> {
        return when (this["type"].asText()) {
            "flervalg" -> this["svar"].toList().map { jsonNode ->
                val jsonAlternativ =
                    (oppslag.lookup(jsonNode.asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt)
                Innsending.SvarAlternativ(
                    jsonAlternativ.text,
                    alertText(jsonAlternativ)
                )
            }
            else -> emptyList()
        }
    }

    private fun alertText(tekstObjekt: Oppslag.TekstObjekt.SvaralternativTekstObjekt): Innsending.InfoTekst? {
        return tekstObjekt.alertText?.let { alerttext ->
            Innsending.Infotype.fraSanityJson(typenøkkel = alerttext.type)?.let { infotype ->
                Innsending.InfoTekst(
                    tittel = alerttext.title,
                    unsafeHtmlBody = Innsending.UnsafeHtml(alerttext.body.html),
                    type = infotype
                )
            }
        }
    }

    private fun JsonNode.generatorfakta(): List<Innsending.SpørmsålOgSvarGruppe> {
        return when (this["type"].asText()) {
            "generator" -> this["svar"].toList().map { liste ->
                Innsending.SpørmsålOgSvarGruppe(
                    liste.toList().map { node ->
                        val tekstObjekt =
                            oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
                        Innsending.SporsmalSvar(
                            sporsmal = tekstObjekt.text,
                            svar = node.svar(),
                            beskrivelse = tekstObjekt.description?.let { rawHtml -> Innsending.UnsafeHtml(rawHtml.html) },
                            hjelpetekst = tekstObjekt.helpText(),
                            oppfølgingspørmål = node.generatorfakta(),

                        )
                    }
                )
            }

            else -> emptyList()
        }
    }

    private fun JsonNode.fakta(): List<Innsending.SporsmalSvar> {
        return this["fakta"].map { node ->
            val tekstObjekt = oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
            Innsending.SporsmalSvar(
                sporsmal = tekstObjekt.text,
                svar = node.svar(),
                beskrivelse = tekstObjekt.description?.let { rawHtmlString -> Innsending.UnsafeHtml(rawHtmlString.html) },
                hjelpetekst = tekstObjekt.helpText(),
                oppfølgingspørmål = node.generatorfakta(),
            )
        }
    }

    fun parse(): Innsending {
        return Innsending(
            seksjoner = parse(innsendingsData),
            generellTekst = oppslag.generellTekst(),
            språk = språk,
            pdfAMetaTagger = oppslag.pdfaMetaTags()
        )
    }
}

private fun JsonNode.booleanTextId(): String {
    val baseId = "${this["beskrivendeId"].asText()}.svar"
    return when (this["svar"].asBoolean()) {
        false -> "$baseId.nei"
        true -> "$baseId.ja"
    }
}

private fun JsonNode.dokumentTekst(): String {
    val opplastetDato = this["svar"]["lastOppTidsstempel"].asLocalDateTime().dagMånedÅr()
    val filnavn = this["svar"]["urn"].asText().split("/").last()
    return "Du har lastet opp $filnavn den $opplastetDato"
}

private fun LocalDate.dagMånedÅr(): String =
    this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

private fun LocalDateTime.dagMånedÅr(): String =
    this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

private fun Oppslag.TekstObjekt.helpText(): Innsending.Hjelpetekst? {
    return this.helpText?.let { Innsending.Hjelpetekst(Innsending.UnsafeHtml(it.body.html), it.title) }
}
