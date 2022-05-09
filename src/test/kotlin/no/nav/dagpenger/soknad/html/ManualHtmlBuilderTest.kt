package no.nav.dagpenger.soknad.html

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class ManualHtmlBuilderTest {

    @Test
    fun manuellTest() {
        assertDoesNotThrow {
            HtmlBuilder.lagHtml(TestModellHtml.htmlModell).also {
                File("build/tmp/test/søknad.html").writeText(it)
            }
        }
    }
}
