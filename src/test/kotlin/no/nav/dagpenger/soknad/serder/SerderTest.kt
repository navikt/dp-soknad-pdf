package no.nav.dagpenger.soknad.serder

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
            assertEquals(
                "Her blir det spurt om noe som du kan svare ja eller nei på. Svarer du ja eller nei?",
                it.text
            )
            assertEquals("Hjelpetekst", it.helpText)
            assertNull(it.description)
        }
    }

    @Test
    fun `lager riktig html og pfd fra json`() {
        assertDoesNotThrow {
            val h = JsonHtmlMapper(
                "ident",
                søknadsData = faktaJson,
                tekst = tekstJson,
                språk = HtmlModell.SøknadSpråk.BOKMÅL
            ).parse()
            HtmlBuilder.lagBruttoHtml(h).also {
                File("build/tmp/test/søknad2.html").writeText(it)
                PdfBuilder.lagPdf(it).also { generertPdf ->
                    File("build/tmp/test/søknad2.pdf").writeBytes(generertPdf)
                }
            }
        }
    }
}
