package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import no.nav.dagpenger.soknad.html.Innsending

private val logger = KotlinLogging.logger { }

internal class Oppslag(private val tekstJson: String) {
    private val objectMapper = jacksonObjectMapper()
    private val tekstMap = parse(tekstJson)
    fun lookup(id: String): TekstObjekt = tekstMap[id] ?: throw IllegalArgumentException("Fant ikke tekst til id $id")

    private fun parse(tekstJson: String): Map<String, TekstObjekt> =
        objectMapper.readTree(tekstJson).let {
            it.tilFaktaTekstObjekt() + it.tilSeksjonTekstObjekt() + it.tilSvarAlternativTekstObjekt()
        }

    private fun JsonNode.tilFaktaTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        fakta().forEach { tekst ->
            val textId = tekst["textId"].asText()
            map[textId] = TekstObjekt.FaktaTekstObjekt(
                textId = textId,
                text = tekst["text"].asText(),
                description = tekst.get("description")?.asText(),
                helpText = tekst.helpText(),
                unit = tekst.get("unit")?.asText()
            )
        }
        return map
    }

    private fun JsonNode.tilSeksjonTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        seksjoner().forEach { tekst ->
            val textId = tekst["textId"].asText()
            map[textId] = TekstObjekt.SeksjonTekstObjekt(
                textId = textId,
                title = tekst["title"].asText(),
                description = tekst.get("description")?.asText(),
                helpText = tekst.helpText()
            )
        }
        return map
    }

    private fun JsonNode.tilSvarAlternativTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        svaralternativer().forEach { tekst ->
            val textId = tekst["textId"].asText()
            map[textId] = TekstObjekt.SvaralternativTekstObjekt(
                textId = textId,
                text = tekst["text"].asText(),
                alertText = tekst["alertText"]?.takeIf { !it.isNull }?.let { alerttext ->
                    TekstObjekt.AlertText(
                        alerttext["title"]?.asText(),
                        alerttext["type"].asText(),
                        alerttext["body"].asText()
                    )
                }
            )
        }
        return map
    }

    internal fun generellTekst(): Innsending.GenerellTekst {
        return objectMapper.readTree(tekstJson).apptekster().let {
            Innsending.GenerellTekst(
                hovedOverskrift = it["pdf.hovedoverskrift"].asText(),
                tittel = it["pdf.tittel"].asText(),
                svar = it["pdf.svar"].asText(),
                datoSendt = it["pdf.datosendt"].asText(),
                fnr = it["pdf.fnr"].asText()
            )
        }
    }

    sealed class TekstObjekt(val textId: String, val description: String?, val helpText: HelpText?) {
        class FaktaTekstObjekt(
            val unit: String? = null,
            val text: String,
            textId: String,
            description: String? = null,
            helpText: HelpText? = null,
        ) : TekstObjekt(textId, description, helpText)

        class SeksjonTekstObjekt(
            val title: String,
            textId: String,
            description: String? = null,
            helpText: HelpText? = null,
        ) : TekstObjekt(textId, description, helpText)

        class SvaralternativTekstObjekt(val text: String, val alertText: AlertText?, textId: String) :
            TekstObjekt(textId, null, null)

        class AlertText(val title: String?, val type: String, val body: String) {
            init {
                if (!listOf("info", "warning", "error", "succes").contains(type)) {
                    throw IllegalArgumentException("Ukjent type for alertText $type")
                }
            }
        }

        class HelpText(val title: String?, val body: String)
    }
}

private fun JsonNode.helpText(): Oppslag.TekstObjekt.HelpText? =
    get("helpText")?.takeIf { !it.isNull }?.let {
        println(it)
        Oppslag.TekstObjekt.HelpText(it.get("title")?.asText(), it.get("body").asText())
    }

private fun JsonNode.seksjoner() = this["sanityTexts"]["seksjoner"]
private fun JsonNode.svaralternativer() = this["sanityTexts"]["svaralternativer"]
private fun JsonNode.fakta() = this["sanityTexts"]["fakta"]
private fun JsonNode.apptekster(): JsonNode = this["sanityTexts"]["apptekster"]
