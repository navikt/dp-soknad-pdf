package no.nav.dagpenger.innsending.html

import kotlinx.html.div
import no.nav.dagpenger.innsending.serder.RawHtmlString
import kotlin.test.Test
import kotlin.test.assertEquals

class InnsendingTest {
    @Test
    fun `legger til klasse i string`() {
        val testHtml = "<p>Somethings gotta give</p>"
        val testHtmlMedKlasse = """<p class="et-eller-annet">Somethings gotta give</p>"""
        Innsending.UnsafeHtml(RawHtmlString.nyEllerNull(testHtml)!!.html).also {
            assertEquals(testHtml, it.kode)
            assertEquals(testHtmlMedKlasse, it.medCssKlasse("et-eller-annet"))
        }
    }
}
