package no.nav.dagpenger.soknad.html

import no.nav.dagpenger.soknad.html.TestHtml.testHtml
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class ManualHtmlBuilderTest {

    @Test
    fun manuellTest() {
        assertDoesNotThrow {
            testHtml.also {
                File("build/tmp/test/søknad.html").writeText(it)
            }
        }
    }
}
