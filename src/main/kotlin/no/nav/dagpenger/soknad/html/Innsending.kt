package no.nav.dagpenger.soknad.html

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal data class Innsending(
    val seksjoner: List<Seksjon>,
    val generellTekst: GenerellTekst,
    val språk: InnsendingsSpråk
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
        val svar: Svar,
        val beskrivelse: String? = null,
        val hjelpetekst: Hjelpetekst? = null,
        val oppfølgingspørmål: List<SpørmsålOgSvarGruppe> = emptyList(),
    )

    data class SpørmsålOgSvarGruppe(val spørsmålOgSvar: List<SporsmalSvar>)

    sealed class Svar
    data class EnkeltSvar(val tekst: String) : Svar()
    data class FlerSvar(val alternativ: List<SvarAlternativ>) : Svar()
    data class SvarAlternativ(val tekst: String, val tilleggsinformasjon: InfoTekst?)
    object IngenSvar : Svar()

    data class Hjelpetekst(val tekst: String, val tittel: String? = null)
    data class InfoTekst(val tittel: String?, val tekst: String, val type: Infotype)

    data class GenerellTekst(
        val hovedOverskrift: String,
        val tittel: String,
        val svar: String,
        val datoSendt: String,
        val fnr: String
    )

    data class InfoBlokk(val fødselsnummer: String, val innsendtTidspunkt: LocalDateTime) {
        val datoSendt = innsendtTidspunkt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
    }

    enum class Infotype() {
        INFORMASJON, ADVARSEL, FEIL;

        companion object {
            fun fraSanityJson(typenøkkel: String) = when (typenøkkel) {
                "info" -> Infotype.INFORMASJON
                "error" -> FEIL
                "warning" -> ADVARSEL
                "success" -> null
                else -> {
                    throw IllegalArgumentException("ukjent alerttekst type $typenøkkel")
                }
            }
        }
    }

    enum class InnsendingsSpråk(
        val langAtributt: String,
        val boolean: (Boolean) -> String
    ) {
        BOKMÅL(
            "no",
            { b: Boolean ->
                if (b) "Ja" else "Nei"
            }
        ),
        ENGELSK(
            "en",
            { b: Boolean ->
                if (b) "Yes" else "No"
            }
        )
    }
}
