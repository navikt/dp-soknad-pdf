package no.nav.dagpenger.innsending.serder

import no.nav.dagpenger.innsending.html.HtmlBuilder
import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.html.Innsending.Hjelpetekst
import no.nav.dagpenger.innsending.pdf.PdfBuilder
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.FaktaTekstObjekt
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.SeksjonTekstObjekt
import org.intellij.lang.annotations.Language
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.time.LocalDateTime
import kotlin.test.assertNotNull

internal class SerderTest {
    val faktaJson = object {}.javaClass.getResource("/fakta.json")?.readText()!!
    val debugfaktaJson = object {}.javaClass.getResource("/debugfakta.json")?.readText()!!
    val tekstJson = object {}.javaClass.getResource("/tekst.json")?.readText()!!
    val debugtekstJson = object {}.javaClass.getResource("/debugtekst.json")?.readText()!!
    private val oppslag = Oppslag(tekstJson)

    @Test
    fun `Debug test`() {
        val mappetInnsending = JsonHtmlMapper(
            innsendingsData = debugfaktaJson,
            tekst = debugtekstJson,
            språk = Innsending.InnsendingsSpråk.BOKMÅL
        ).parse().apply {
            infoBlokk = Innsending.InfoBlokk("ident", innsendtTidspunkt = LocalDateTime.now())
        }

        assertIngenTommehjelpetekster(mappetInnsending)
        HtmlBuilder.lagNettoHtml(mappetInnsending).also { PdfBuilder.lagPdf(it) }
        HtmlBuilder.lagBruttoHtml(mappetInnsending).also {
            PdfBuilder.lagPdf(it)
            assertEquals(
                0,
                Jsoup.parse(it).getElementsByClass("hjelpetekst").filter { t -> t.childrenSize() <1 }.size,
                "fant tomme hjelpetekster"
            )
        }
    }

    @Test
    fun `parser søknadstekst riktig`() {

        oppslag.lookup("seksjon1").also {
            require(it is SeksjonTekstObjekt)
            assertEquals("seksjon1", it.textId)
            assertEquals("Tittel for seksjon 1", it.title)
            assertEquals("Hjelpetekst med overskrift til seksjon", it.helpText?.title)
            val expcextedBody =
                """<p>Her er en hjelpetekst tekst som hjelper veldig mye når en trenger hjelp. Med superhjelpsom <a href="https://nav.no/superhjelpen">lenke</a></p>"""
            assertEquals(expcextedBody, it.helpText?.body?.html)
            @Language("HTML")
            val expectedDescription =
                """<p>description for seksjon</p><p>tadda, det her går jo bra!</p>"""
            assertEquals(expectedDescription, it.description?.html?.replace("\n", ""))
        }

        oppslag.lookup("f3").also {
            require(it is FaktaTekstObjekt)
            assertEquals("f3", it.textId)
            assertEquals(
                "Her blir det spurt om noe som du kan svare ja eller nei på. Svarer du ja eller nei?",
                it.text
            )
            assertEquals("<p>Hjelpetekst</p>", it.helpText?.body?.html?.replace(" ", ""))
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
                    "<p>Her er ett og annet som er greit å vite hvios du har valgt svaralternativ1</p>",
                    alerttext.body!!.html
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
    fun `lager html og pfd fra json`() {
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

    @Test
    fun `fjerner tags som ikke er tillatt`() {
        oppslag.`portable objekter i testfil`().forEach { tekstobjekt ->
            tekstobjekt.description?.let { assertIngenUlovligeTagger(it) }
            tekstobjekt.helpText?.body?.let { assertIngenUlovligeTagger(it) }
        }
    }
}

private fun assertIngenUlovligeTagger(htmlString: RawHtmlString) {
    listOf("<script>", "<img>", "<iframe>").forEach {
        assertFalse(
            htmlString.html.contains(it.toRegex()),
            "string ${htmlString.html} inneholder ulovlig tag $it"
        )
    }
}

private fun Oppslag.`portable objekter i testfil`() =
    listOf(
        this.lookup("f15"),
        this.lookup("flervalg7"),
        this.lookup("flervalg1"),
        this.lookup("periode10"),
        this.lookup("desimaltall3"),
        this.lookup("f1"),
        this.lookup("f3"),
        this.lookup("f67"),
        this.lookup("seksjon1"),
        this.lookup("svaralternativ1")
    )

private fun assertIngenTommehjelpetekster(innsending: Innsending) {
    innsending.seksjoner.forEach { seksjon ->
        seksjon.hjelpetekst?.let { assertFalse(it.isEmpty(), "Fant tom hjelpetekst i seksjon ${seksjon.overskrift}") }
        seksjon.spmSvar.forEach { spmSvar ->
            spmSvar.hjelpetekst?.let { assertFalse(it.isEmpty(), "Fant tom hjelpetekst på spørsmål ${spmSvar.sporsmal}") }
        }
    }
}

private fun Hjelpetekst.isEmpty(): Boolean = this.tittel.isNullOrEmpty() && this.unsafeHtmlBody.isNullOrEmpty()

private fun Innsending.UnsafeHtml?.isNullOrEmpty(): Boolean = this?.kode.isNullOrEmpty()
