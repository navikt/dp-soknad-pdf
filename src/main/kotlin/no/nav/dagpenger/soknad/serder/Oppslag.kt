package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

internal class Oppslag(tekstJson: String) {
    private val objectMapper = jacksonObjectMapper()
    private val tekstMap = parse(tekstJson)
    fun lookup(id: String): TekstObjekt = tekstMap[id] ?: throw IllegalArgumentException("Fant ikke tekst til id $id")

    private fun JsonNode.tilFaktaTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        this["SanityTexts"]["fakta"].forEach { tekst ->
            val textId = tekst["textId"].asText()
            map[textId] = TekstObjekt.FaktaTekstObjekt(
                textId = textId,
                text = tekst["text"].asText(),
                description = tekst.get("description")?.asText(),
                helpText = tekst.get("helpText")?.asText(),
                unit = tekst.get("unit")?.asText()
            )
        }
        return map
    }

    private fun JsonNode.tilSeksjonTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        this["SanityTexts"]["seksjoner"].forEach { tekst ->
            val textId = tekst["textId"].asText()
            map[textId] = TekstObjekt.SeksjonTekstObjekt(
                textId = textId,
                title = tekst["title"].asText(),
                description = tekst.get("description")?.asText(),
                helpText = tekst.get("helpText")?.asText()
            )
        }
        return map
    }

    private fun parse(tekstJson: String): Map<String, TekstObjekt> =
        objectMapper.readTree(tekstJson).let {
            it.tilFaktaTekstObjekt() + it.tilSeksjonTekstObjekt()
        }

    sealed class TekstObjekt(val textId: String, val description: String?, val helpText: String?) {
        class FaktaTekstObjekt(
            val unit: String? = null,
            val text: String,
            textId: String,
            description: String? = null,
            helpText: String? = null,
        ) : TekstObjekt(textId, description, helpText)

        class SeksjonTekstObjekt(
            val title: String,
            textId: String,
            description: String? = null,
            helpText: String? = null,
        ) : TekstObjekt(textId, description, helpText)
    }
}
