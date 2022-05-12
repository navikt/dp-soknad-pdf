package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.util.reflect.instanceOf
import kotlinx.html.InputType
import no.nav.dagpenger.soknad.html.HtmlBuilder
import no.nav.dagpenger.soknad.html.HtmlModell
import no.nav.dagpenger.soknad.pdf.PdfBuilder
import no.nav.dagpenger.soknad.serder.Oppslag.TekstObjekt.FaktaTekstObjekt
import no.nav.dagpenger.soknad.serder.Oppslag.TekstObjekt.SeksjonTekstObjekt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.time.LocalDate


internal class SerderTest {
    val faktaJson = object {}.javaClass.getResource("/fakta.json")?.readText()!!
    val tekstJson = object {}.javaClass.getResource("/tekst.json")?.readText()!!

    @Test
    fun `parse søknads tekst riktig`() {
        val oppslag = Oppslag(tekstJson)

        oppslag.lookup("seksjon1").also {
            require(it is SeksjonTekstObjekt)
            assertEquals("seksjon1", it.textId)
            assertEquals("Tittel for seksjon 1", it.title)
            assertEquals("Hjelpetekst for seksjon", it.helpText)
            assertEquals("description for seksjon", it.description)
        }

        oppslag.lookup("f3").also {
            require(it is FaktaTekstObjekt)
            assertEquals("f3", it.textId)
            assertEquals("Her blir det spurt om noe som du kan svar ja eller nei på. Svarer du ja eller nei?",
                it.text
            )
            assertEquals("Hjelpetekst", it.helpText)
            assertNull(it.description)
        }
    }

    @Test
    fun hubba() {
        assertDoesNotThrow {
            val h = HUbba(
                "ident",
                søknadsData = faktaJson,
                tekst = tekstJson,
                språk = HtmlModell.SøknadSpråk.BOKMÅL
            ).parse()
            HtmlBuilder.lagHtml(h).also {
                File("build/tmp/test/søknad2.html").writeText(it)
                PdfBuilder.lagPdf(it).also { generertPdf ->
                    File("build/tmp/test/søknad2.pdf").writeBytes(generertPdf)
                }
            }
        }
    }

    internal class HUbba(
        private val ident: String,
        private val søknadsData: String,
        tekst: String,
        private val språk: HtmlModell.SøknadSpråk
    ) {
        private val oppslag = Oppslag(tekst)
        private val objectMapper = jacksonObjectMapper()

        private fun parse(søknadsData: String): List<HtmlModell.Seksjon> {
            return objectMapper.readTree(søknadsData)["seksjoner"].map {
                val tekstObjekt = oppslag.lookup(it["beskrivendeId"].asText()) as SeksjonTekstObjekt
                HtmlModell.Seksjon(
                    overskrift = tekstObjekt.title,
                    description = tekstObjekt.description,
                    helpText = tekstObjekt.helpText,
                    spmSvar = it.fakta()
                )
            }
        }

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
                val tekstObjekt = oppslag.lookup(node["beskrivendeId"].asText()) as FaktaTekstObjekt
                HtmlModell.SporsmalSvar(
                    sporsmal = tekstObjekt.text,
                    svar = node.svar(),
                    infotekst = tekstObjekt.description,
                    hjelpeTekst = tekstObjekt.helpText,
                    oppfølgingspørmål = listOf()
                )
            }
        }

        fun parse(): HtmlModell {
            return HtmlModell(
                seksjoner = parse(søknadsData),
                metaInfo = HtmlModell.MetaInfo(språk = HtmlModell.SøknadSpråk.BOKMÅL),
                pdfAKrav = HtmlModell.PdfAKrav(description = "description", subject = "subject", author = "author"),
                infoBlokk = HtmlModell.InfoBlokk(
                    fødselsnummer = this.ident,
                    datoSendt = "${LocalDate.now()}"
                ) // todo finne ut hvordan vi får tak i innsendt dato.
            )
        }
    }
}
