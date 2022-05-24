package no.nav.dagpenger.soknad.html

import no.nav.dagpenger.soknad.pdf.PdfBuilder.lagPdf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class HtmlBuilderTest {

    @Disabled
    @Test
    fun manuellTest() {
        assertDoesNotThrow {
            HtmlBuilder.lagBruttoHtml(TestModellHtml.innsendtSøknad).also {
                File("build/tmp/test/søknad.html").writeText(it)
                lagPdf(it).also { generertPdf ->
                    File("build/tmp/test/søknad.pdf").writeBytes(generertPdf)
                }
            }
        }
    }

    @Test
    fun `lager netto html`() {
        HtmlBuilder.lagNettoHtml(TestModellHtml.innsendtSøknad).also {
            assertEquals(0, "class=\"infotekst\"".toRegex().findAll(it).count())
            assertEquals(0, "class=\"hjelpetekst\"".toRegex().findAll(it).count())
            assertEquals(4, "class=\"seksjon\"".toRegex().findAll(it).count())
            //     File("build/tmp/test/netto.html").writeBytes(it.toByteArray())
        }
    }

    @Test
    fun `lager brutto html`() {
        HtmlBuilder.lagBruttoHtml(TestModellHtml.innsendtSøknad).also {
            assertEquals(6, "class=\"infotekst\"".toRegex().findAll(it).count())
            assertEquals(6, "class=\"hjelpetekst\"".toRegex().findAll(it).count())
            assertEquals(4, "class=\"seksjon\"".toRegex().findAll(it).count())
            //    File("build/tmp/test/brutto.html").writeBytes(it.toByteArray())
        }
    }
}
