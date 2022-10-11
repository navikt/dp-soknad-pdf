package no.nav.dagpenger.innsending.serder

import io.kotest.matchers.shouldNotBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class OppslagTest {
    @Language("JSON")
    private val tommeTekster = """
       {
  "sanityTexts": {
    "fakta": [
    ],
    "seksjoner": [
    ],
    "svaralternativer": [
    ],
    "apptekster": [
    ],
    "dokumentkrav": [
    ]
  }
}
 
    """.trimIndent()

    @Test
    fun `Lager default tekst objekter`() {
        Oppslag(tommeTekster).let { oppslag ->
            oppslag.lookup<Oppslag.TekstObjekt.EnkelText>("id") shouldNotBe null
            oppslag.lookup<Oppslag.TekstObjekt.SvaralternativTekstObjekt>("id") shouldNotBe null
            oppslag.lookup<Oppslag.TekstObjekt.DokumentkravTekstObjekt>("id") shouldNotBe null
            oppslag.lookup<Oppslag.TekstObjekt.SeksjonTekstObjekt>("id") shouldNotBe null
            oppslag.lookup<Oppslag.TekstObjekt.FaktaTekstObjekt>("id") shouldNotBe null
        }
    }
}
