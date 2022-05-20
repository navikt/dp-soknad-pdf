package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.soknad.html.HtmlModell

internal class JsonHtmlMapper(
    private val søknadsData: String,
    tekst: String,
    private val språk: HtmlModell.SøknadSpråk = HtmlModell.SøknadSpråk.BOKMÅL
) {
    private val oppslag = Oppslag(tekst)
    private val objectMapper = jacksonObjectMapper()

    private fun parse(søknadsData: String): List<HtmlModell.Seksjon> {
        return objectMapper.readTree(søknadsData)["seksjoner"].map {
            val tekstObjekt = oppslag.lookup(it["beskrivendeId"].asText()) as Oppslag.TekstObjekt.SeksjonTekstObjekt
            HtmlModell.Seksjon(
                overskrift = tekstObjekt.title,
                beskrivelse = tekstObjekt.description,
                hjelpetekst = tekstObjekt.helpText(),
                spmSvar = it.fakta()
            )
        }
    }

    // TODO andre fakatyper
    private fun JsonNode.svar(): String {
        return when (this["type"].asText()) {
            "string" -> this["svar"].asText()
            "boolean" -> språk.boolean(this["svar"].asBoolean())
            "generator" -> "generator"
            else -> throw IllegalArgumentException("hubba")
        }
    }

    private fun JsonNode.fakta(): List<HtmlModell.SporsmalSvar> {
        return this["fakta"].map { node ->
            val tekstObjekt = oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
            HtmlModell.SporsmalSvar(
                sporsmal = tekstObjekt.text,
                svar = node.svar(),
                beskrivelse = tekstObjekt.description,
                hjelpetekst = tekstObjekt.helpText(),
                oppfølgingspørmål = listOf()
            )
        }
    }

    fun parse(): HtmlModell {
        return HtmlModell(
            seksjoner = parse(søknadsData),
            metaInfo = HtmlModell.MetaInfo(språk = HtmlModell.SøknadSpråk.BOKMÅL),
        )
    }
}

private fun Oppslag.TekstObjekt.helpText(): HtmlModell.Hjelpetekst? {
    return this.helpText?.let { HtmlModell.Hjelpetekst(it.body, it.title) }
}
