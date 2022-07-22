package no.nav.dagpenger.innsending.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import mu.withLoggingContext
import no.nav.dagpenger.innsending.html.Innsending
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

private val logger = KotlinLogging.logger { }

internal class Oppslag(private val tekstJson: String) {
    private val objectMapper = jacksonObjectMapper()
    private val tekstMap = parse(tekstJson)
    fun lookup(id: String): TekstObjekt = tekstMap[id] ?: throw IllegalArgumentException("Fant ikke tekst til id $id")

    companion object {
        private val logg = KotlinLogging.logger {}
    }

    private fun parse(tekstJson: String): Map<String, TekstObjekt> {
        return try {
            objectMapper.readTree(tekstJson).let {
                it.tilFaktaTekstObjekt() + it.tilSeksjonTekstObjekt() + it.tilSvarAlternativTekstObjekt() + it.tilAppTekstObjekt()
            }
        } catch (e: NullPointerException) {
            logg.error(e) { "Fikk NullPointerException ved parsing av tekster: $tekstJson" }
            throw e
        }
    }

    private fun JsonNode.tilFaktaTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        fakta().forEach { tekst ->
            val textId = tekst["textId"].asText()
            withLoggingContext("textId" to textId) {
                map[textId] = TekstObjekt.FaktaTekstObjekt(
                    textId = textId,
                    text = tekst["text"].asText(),
                    description = tekst.get("description")?.asRawHtmlString(),
                    helpText = tekst.hjelpetekst(),
                    unit = tekst.get("unit")?.asText()
                )
            }
        }
        return map
    }

    private fun JsonNode.tilSeksjonTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        seksjoner().forEach { tekst ->
            val textId = tekst["textId"].asText()
            withLoggingContext("textId" to textId) {
                map[textId] = TekstObjekt.SeksjonTekstObjekt(
                    textId = textId,
                    title = tekst["title"].asText(),
                    description = tekst.get("description")?.asRawHtmlString(),
                    helpText = tekst.hjelpetekst()
                )
            }
        }
        return map
    }

    private fun JsonNode.tilAppTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        apptekster().forEach { tekst ->
            val textId = tekst["textId"].asText()
            withLoggingContext("textId" to textId) {
                map[textId] = TekstObjekt.EnkelText(
                    textId = textId,
                    text = tekst["valueText"].asText()
                )
            }
        }
        return map
    }

    private fun JsonNode.tilSvarAlternativTekstObjekt(): Map<String, TekstObjekt> {
        val map = mutableMapOf<String, TekstObjekt>()
        svaralternativer().forEach { tekst ->
            val textId = tekst["textId"].asText()
            withLoggingContext("textId" to textId) {
                map[textId] = TekstObjekt.SvaralternativTekstObjekt(
                    textId = textId,
                    text = tekst["text"].asText(),
                    alertText = tekst["alertText"]?.takeIf { !it.isNull }?.let { alerttext ->
                        TekstObjekt.AlertText(
                            alerttext["title"]?.asText(),
                            alerttext["type"].asText(),
                            alerttext["body"].asRawHtmlString()
                        )
                    }
                )
            }
        }
        return map
    }

    internal fun generellTekst(): Innsending.GenerellTekst {
        return Innsending.GenerellTekst(
            hovedOverskrift = (lookup("pdf.hovedoverskrift") as TekstObjekt.EnkelText).text,
            tittel = (lookup("pdf.tittel") as TekstObjekt.EnkelText).text,
            svar = (lookup("pdf.svar") as TekstObjekt.EnkelText).text,
            datoSendt = (lookup("pdf.datosendt") as TekstObjekt.EnkelText).text,
            fnr = (lookup("pdf.fnr") as TekstObjekt.EnkelText).text
        )
    }

    fun pdfaMetaTags(): Innsending.PdfAMetaTagger =
        objectMapper.readTree(tekstJson).apptekster().let {
            Innsending.PdfAMetaTagger(
                description = (lookup("pdfa.description") as TekstObjekt.EnkelText).text,
                subject = (lookup("pdfa.subject") as TekstObjekt.EnkelText).text,
                author = (lookup("pdfa.author") as TekstObjekt.EnkelText).text
            )
        }

    sealed class TekstObjekt(val textId: String, val description: RawHtmlString?, val helpText: HelpText?) {
        class FaktaTekstObjekt(
            val unit: String? = null,
            val text: String,
            textId: String,
            description: RawHtmlString? = null,
            helpText: HelpText? = null
        ) : TekstObjekt(textId, description, helpText)

        class SeksjonTekstObjekt(
            val title: String,
            textId: String,
            description: RawHtmlString? = null,
            helpText: HelpText? = null
        ) : TekstObjekt(textId, description, helpText)

        class SvaralternativTekstObjekt(val text: String, val alertText: AlertText?, textId: String) :
            TekstObjekt(textId, null, null)

        class EnkelText(textId: String, val text: String) : TekstObjekt(textId, null, null)

        class AlertText(val title: String?, val type: String, val body: RawHtmlString) {
            init {
                if (!listOf("info", "warning", "error", "succes").contains(type)) {
                    throw IllegalArgumentException("Ukjent type for alertText $type")
                }
            }
        }

        class HelpText(val title: String?, val body: RawHtmlString?)
    }
}

private fun JsonNode.asRawHtmlString(): RawHtmlString {
    return if (this is TextNode) {
        RawHtmlString(this.asText())
    } else {
        RawHtmlString(this.toList().joinToString(separator = "") { it.asText() })
    }
}

class RawHtmlString(htmlFraSanity: String) {
    val html: String = Jsoup.clean(htmlFraSanity, tilatteTaggerOgAttributter).also {
        if (!(it.startsWith("<") && it.endsWith(""))) {
            logger.error { "Mottok html med ustøttet innhold: \noriginal html: $htmlFraSanity\n etter clean: $htmlFraSanity" }
            throw UgyldigHtmlError(htmlFraSanity)
        }
    }

    companion object {
        private val tilatteTaggerOgAttributter = Safelist.relaxed().removeTags("img")
    }
}

private fun JsonNode.hjelpetekst(): Oppslag.TekstObjekt.HelpText? =
    get("helpText")?.let {
        Oppslag.TekstObjekt.HelpText(it.get("title")?.asText(), it.get("body")?.asRawHtmlString())
    }

private fun JsonNode.seksjoner() = this["sanityTexts"]["seksjoner"]
private fun JsonNode.svaralternativer() = this["sanityTexts"]["svaralternativer"]
private fun JsonNode.fakta() = this["sanityTexts"]["fakta"]
private fun JsonNode.apptekster(): JsonNode = this["sanityTexts"]["apptekster"]

class UgyldigHtmlError(htmlString: String) : IllegalArgumentException("Mottok ugyldig HTML: $htmlString")
