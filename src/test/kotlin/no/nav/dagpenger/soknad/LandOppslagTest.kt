package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.html.InnsendtSøknad
import no.nav.dagpenger.soknad.html.InnsendtSøknad.SøknadSpråk.BOKMÅL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class LandOppslagTest {

    @Test
    fun `henter riktig land på riktig språk`() {
        LandOppslag.hentLand(InnsendtSøknad.SøknadSpråk.ENGELSK, "NOR").also {
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
