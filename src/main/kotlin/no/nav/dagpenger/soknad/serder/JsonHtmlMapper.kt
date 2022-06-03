package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.soknad.html.InnsendtSøknad
import no.nav.dagpenger.soknad.html.InnsendtSøknad.EnkeltSvar
import no.nav.dagpenger.soknad.html.InnsendtSøknad.Svar

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

    // TODO andre fakatyper
    private fun JsonNode.svar(): Svar {
        return when (this["type"].asText()) {
            "string" -> EnkeltSvar(this["svar"].asText())
            "boolean" -> EnkeltSvar(språk.boolean(this["svar"].asBoolean()))
            "generator" -> InnsendtSøknad.IngenSvar
            "envalg" -> EnkeltSvar((oppslag.lookup(this["svar"].asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt).text)
            "flervalg" -> InnsendtSøknad.FlerSvar(this.flerValg())
            else -> throw IllegalArgumentException("Ukjent faktumtype")
        }
    }

    private fun JsonNode.flerValg(): List<String> {
        return when (this["type"].asText()) {
            "flervalg" -> this["svar"].toList().map {
                (oppslag.lookup(it.asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt).text
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
                oppfølgingspørmål = listOf(),
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

private fun Oppslag.TekstObjekt.helpText(): InnsendtSøknad.Hjelpetekst? {
    return this.helpText?.let { InnsendtSøknad.Hjelpetekst(it.body, it.title) }
}
