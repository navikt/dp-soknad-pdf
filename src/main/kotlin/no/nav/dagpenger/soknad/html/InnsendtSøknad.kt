package no.nav.dagpenger.soknad.html

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal data class InnsendtSøknad(
    val seksjoner: List<Seksjon>,
    val metaInfo: MetaInfo
) {

    lateinit var infoBlokk: InfoBlokk

    object PdfAMetaTagger {
        const val description: String = "Søknad om dagpenger"
        const val subject: String = "Dagpenger"
        const val author: String = "NAV"
    }

    data class Seksjon(
        val overskrift: String,
        val beskrivelse: String? = null,
        val hjelpetekst: Hjelpetekst? = null,
        val spmSvar: List<SporsmalSvar>
    )

    data class SporsmalSvar(
        val sporsmal: String,
        val svar: String?,
        val beskrivelse: String? = null,
        val hjelpetekst: Hjelpetekst? = null,
        val oppfølgingspørmål: List<SporsmalSvar>? = null,
        val flereSvar: List<String> = listOf()
    )

    data class Hjelpetekst(val tekst: String, val tittel: String? = null)

    data class MetaInfo(
        val språk: SøknadSpråk = SøknadSpråk.BOKMÅL,
        val hovedOverskrift: String = språk.hovedOverskrift,
        val tittel: String = språk.tittel,
    )

    data class InfoBlokk(val fødselsnummer: String, val innsendtTidspunkt: LocalDateTime) {
        val datoSendt = innsendtTidspunkt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    }

    enum class SøknadSpråk(
        val langAtributt: String,
        val svar: String,
        val fødselsnummer: String,
        val datoSendt: String,
        val hovedOverskrift: String,
        val tittel: String,
        val boolean: (Boolean) -> String
    ) {
        BOKMÅL(
            "no",
            "Svar",
            "Fødselsnummer",
            "Dato sendt",
            "Søknad om dagpenger",
            "Søknad om dagpenger",
            { b: Boolean ->
                if (b) "Ja" else "Nei"
            }
        )
    }
}
