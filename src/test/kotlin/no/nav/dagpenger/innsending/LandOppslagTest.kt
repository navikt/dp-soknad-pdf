package no.nav.dagpenger.innsending

import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.html.Innsending.InnsendingsSpråk.BOKMÅL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LandOppslagTest {

    @Test
    fun `henter riktig land på riktig språk`() {
        LandOppslag.hentLand(Innsending.InnsendingsSpråk.ENGELSK, "NOR").also {
            assertEquals("Norway", it)
        }
        LandOppslag.hentLand(BOKMÅL, "bel").also {
            assertEquals("Belgia", it)
        }
        assertThrows<IllegalArgumentException> {
            LandOppslag.hentLand(BOKMÅL, "nei")
        }
    }
}
