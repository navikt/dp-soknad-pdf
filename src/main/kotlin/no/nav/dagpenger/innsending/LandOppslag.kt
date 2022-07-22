package no.nav.dagpenger.innsending

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.innsending.html.Innsending.InnsendingsSpråk
import no.nav.dagpenger.innsending.pdf.fileAsString

object LandOppslag {
    // countries.json hentet fra https://github.com/stefangabos/world_countries
    private val data by lazy {
        "/countries.json".fileAsString().let {
            jacksonObjectMapper().readTree(it)
        }
    }

    internal fun hentLand(språk: InnsendingsSpråk, iso3landkode: String): String =
        data.find { it["alpha3"].asText() == iso3landkode.lowercase() }?.let {
            it[språk.langAtributt].asText()
        } ?: throw IllegalArgumentException("Fant ikke land med alpha3kode $iso3landkode")
}
