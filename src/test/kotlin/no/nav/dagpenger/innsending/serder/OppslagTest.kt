package no.nav.dagpenger.innsending.serder

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

internal class OppslagTest {
    @Test
    fun `Lager default tekst objekter`() {
        Oppslag(testTekster).let { oppslag ->
            shouldNotThrowAny {
                oppslag.lookup<Oppslag.TekstObjekt.EnkelText>("id")
                oppslag.lookup<Oppslag.TekstObjekt.SvaralternativTekstObjekt>("id")
                oppslag.lookup<Oppslag.TekstObjekt.DokumentkravTekstObjekt>("id")
                oppslag.lookup<Oppslag.TekstObjekt.SeksjonTekstObjekt>("id")
                oppslag.lookup<Oppslag.TekstObjekt.FaktaTekstObjekt>("id")
            }
        }
    }

    @Test
    fun `Håndterer feil type`() {
        Oppslag(testTekster).lookup<Oppslag.TekstObjekt.DokumentkravTekstObjekt>("id2").title shouldBe "id2"
    }

    @Language("JSON")
    private val testTekster =
        """
               {
          "sanityTexts": {
            "fakta": [
            ],
            "seksjoner": [
            ],
            "svaralternativer": [
            ],
            "apptekster": [
             {
                "textId": "id2",
                "valueText": "Legg til barn"
              }
            ],
            "dokumentkrav": [
            ]
          }
        }
        """.trimIndent()
}
