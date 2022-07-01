package no.nav.dagpenger.innsending.serder

import no.nav.dagpenger.innsending.html.HtmlBuilder
import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.pdf.PdfBuilder
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.FaktaTekstObjekt
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.SeksjonTekstObjekt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class SerderTest {
    val faktaJson = object {}.javaClass.getResource("/fakta.json")?.readText()!!
    val tekstJson = object {}.javaClass.getResource("/tekst.json")?.readText()!!
    private val oppslag = Oppslag(tekstJson)

    @Test
    fun `parser søknadstekst riktig`() {

        oppslag.lookup("seksjon1").also {
            require(it is SeksjonTekstObjekt)
            assertEquals("seksjon1", it.textId)
            assertEquals("Tittel for seksjon 1", it.title)
            assertEquals("Hjelpetekst med overskrift til seksjon", it.helpText?.title)
            assertEquals("Her er en hjelpetekst tekst som hjelper veldig mye når en trenger hjelp", it.helpText?.body)
            assertEquals("description for seksjon", it.description)
        }

        oppslag.lookup("f3").also {
            require(it is FaktaTekstObjekt)
            assertEquals("f3", it.textId)
            assertEquals(
                "Her blir det spurt om noe som du kan svare ja eller nei på. Svarer du ja eller nei?",
                it.text
            )
            assertEquals("Hjelpetekst", it.helpText?.body)
            assertNull(it.description)
        }
    }

    @Test
    fun `parser svaralternativ riktig`() {
        oppslag.lookup("svaralternativ1").also { oppslag ->
            require(oppslag is Oppslag.TekstObjekt.SvaralternativTekstObjekt)
            assertEquals("svaralternativ1", oppslag.textId)
            assertEquals(
                "Vet ikke helt hva dte her skal brukes til enda, men gjetter på at vi finner det ut",
                oppslag.text
            )
            require(oppslag.alertText != null)
            oppslag.alertText.also { alerttext ->
                assertEquals(
                    "Her er ett og annet som er greit å vite hvios du har valgt svaralternativ1", alerttext.body
                )
                assertEquals("Her er noe info", alerttext.title)
                assertEquals("info", alerttext.type)
            }
        }
        assertDoesNotThrow {
            oppslag.lookup("flervalg1").also {
                require(it is Oppslag.TekstObjekt.SvaralternativTekstObjekt)
                assertNotNull(it.alertText)
                assertNull(it.alertText.title)
            }
            oppslag.lookup("flervalg2").also {
                require(it is Oppslag.TekstObjekt.SvaralternativTekstObjekt)
                assertNull(it.alertText)
            }
        }
    }

    @Test
    fun `lager riktig html og pfd fra json`() {
        assertDoesNotThrow {
            val h = JsonHtmlMapper(
                innsendingsData = faktaJson,
                tekst = tekstJson,
                språk = Innsending.InnsendingsSpråk.BOKMÅL
            ).parse().apply {
                infoBlokk = Innsending.InfoBlokk("ident", innsendtTidspunkt = LocalDateTime.now())
            }
            HtmlBuilder.lagBruttoHtml(h).also {
                File("build/tmp/test/søknad2.html").writeText(it)
                PdfBuilder.lagPdf(it).also { generertPdf ->
                    File("build/tmp/test/søknad2.pdf").writeBytes(generertPdf)
                }
            }
        }
    }
}
