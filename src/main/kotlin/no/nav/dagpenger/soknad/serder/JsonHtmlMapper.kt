package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.soknad.LandOppslag
import no.nav.dagpenger.soknad.html.InnsendtSøknad
import no.nav.dagpenger.soknad.html.InnsendtSøknad.EnkeltSvar
import no.nav.dagpenger.soknad.html.InnsendtSøknad.Svar
import no.nav.helse.rapids_rivers.asLocalDate
import java.time.format.DateTimeFormatter

internal class JsonHtmlMapper(
    private val søknadsData: String,
    tekst: String,
    private val språk: InnsendtSøknad.SøknadSpråk = InnsendtSøknad.SøknadSpråk.BOKMÅL,
) {
    private val oppslag = Oppslag(tekst)
    private val objectMapper = jacksonObjectMapper()

    private fun parse(søknadsData: String): List<InnsendtSøknad.Seksjon> {
        return objectMapper.readTree(søknadsData)["seksjoner"].map {
            val tekstObjekt = oppslag.lookup(it["beskrivendeId"].asText()) as Oppslag.TekstObjekt.SeksjonTekstObjekt
            InnsendtSøknad.Seksjon(
                overskrift = tekstObjekt.title,
                beskrivelse = tekstObjekt.description,
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
            "boolean" -> EnkeltSvar(språk.boolean(this["svar"].asBoolean()))
            "localdate" -> EnkeltSvar(this["svar"].dagMånedÅr())
            "periode" -> EnkeltSvar("${this["svar"]["fom"].dagMånedÅr()} - ${this["svar"]["tom"].dagMånedÅr()}")
            "generator" -> InnsendtSøknad.IngenSvar
            "envalg" -> EnkeltSvar((oppslag.lookup(this["svar"].asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt).text)
            "flervalg" -> InnsendtSøknad.FlerSvar(this.flerValg())
            "land" -> EnkeltSvar(LandOppslag.hentLand(språk, this["svar"].asText()))
            else -> throw IllegalArgumentException("Ukjent faktumtype $type")
        }
    }

    private fun JsonNode.flerValg(): List<InnsendtSøknad.SvarAlternativ> {
        return when (this["type"].asText()) {
            "flervalg" -> this["svar"].toList().map { jsonNode ->
                val jsonAlternativ =
                    (oppslag.lookup(jsonNode.asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt)
                InnsendtSøknad.SvarAlternativ(
                    jsonAlternativ.text,
                    alertText(jsonAlternativ)
                )
            }
            else -> emptyList()
        }
    }

    private fun alertText(tekstObjekt: Oppslag.TekstObjekt.SvaralternativTekstObjekt): InnsendtSøknad.InfoTekst? {
        return tekstObjekt.alertText?.let { alerttext ->
            infoTypefraSanityJson(typenøkkel = alerttext.type)?.let { infotype ->
                InnsendtSøknad.InfoTekst(
                    tittel = alerttext.title,
                    tekst = alerttext.body,
                    type = infotype
                )
            }
        }
    }
    private fun infoTypefraSanityJson(typenøkkel: String) = when (typenøkkel) {
        "info" -> InnsendtSøknad.Infotype.INFORMASJON
        "error" -> InnsendtSøknad.Infotype.FEIL
        "warning" -> InnsendtSøknad.Infotype.ADVARSEL
        "success" -> null
        else -> { throw IllegalArgumentException("ukjent alerttekst type $typenøkkel") }
    }

    private fun JsonNode.generatorfakta(): List<InnsendtSøknad.SpørmsålOgSvarGruppe> {
        return when (this["type"].asText()) {
            "generator" -> this["svar"].toList().map { liste ->
                InnsendtSøknad.SpørmsålOgSvarGruppe(
                    liste.toList().map { node ->
                        val tekstObjekt =
                            oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
                        InnsendtSøknad.SporsmalSvar(
                            sporsmal = tekstObjekt.text,
                            svar = node.svar(),
                            beskrivelse = tekstObjekt.description,
                            hjelpetekst = tekstObjekt.helpText(),
                            oppfølgingspørmål = node.generatorfakta(),

                        )
                    }
                )
            }

            else -> emptyList()
        }
    }

    private fun JsonNode.fakta(): List<InnsendtSøknad.SporsmalSvar> {
        return this["fakta"].map { node ->
            val tekstObjekt = oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
            InnsendtSøknad.SporsmalSvar(
                sporsmal = tekstObjekt.text,
                svar = node.svar(),
                beskrivelse = tekstObjekt.description,
                hjelpetekst = tekstObjekt.helpText(),
                oppfølgingspørmål = node.generatorfakta(),
            )
        }
    }

    fun parse(): InnsendtSøknad {
        return InnsendtSøknad(
            seksjoner = parse(søknadsData),
            metaInfo = InnsendtSøknad.MetaInfo(språk = InnsendtSøknad.SøknadSpråk.BOKMÅL),
        )
    }
}

private fun JsonNode.dagMånedÅr(): String =
    this.asLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

private fun Oppslag.TekstObjekt.helpText(): InnsendtSøknad.Hjelpetekst? {
    return this.helpText?.let { InnsendtSøknad.Hjelpetekst(it.body, it.title) }
}
