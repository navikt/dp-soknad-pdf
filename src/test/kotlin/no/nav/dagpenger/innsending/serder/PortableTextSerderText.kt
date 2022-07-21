package no.nav.dagpenger.innsending.serder

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class PortableTextSerderText {
    val faktaJson = object {}.javaClass.getResource("/portabletext/portabletextfakta.json")?.readText()!!
    val tekstJson = object {}.javaClass.getResource("/portabletext/portabletextobjects.json")?.readText()!!

    @Test
    fun `håndterer enkel portable text blokk`() {
        val oppslag = Oppslag(tekstJson)
        val seksjonsText = oppslag.lookup("seksjon1")
        require(seksjonsText.description != null).also {
            assertFalse(seksjonsText.description.isEmpty())
        }
    }

}

