package no.nav.dagpenger.soknad.html

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal data class HtmlModell(
    val seksjoner: List<Seksjon>,
    val metaInfo: MetaInfo,
    val pdfAKrav: PdfAKrav,
    val infoBlokk: InfoBlokk
) {
    // TODO: Både beskrivelse og hjelpetekst kan ha tittel

    data class Seksjon(
        val overskrift: String,
        val beskrivelse: String? = null,
        val hjelpetekst: String? = null,
        val spmSvar: List<SporsmalSvar>
    )

    data class SporsmalSvar(
        val sporsmal: String,
        val svar: String,
        val beskrivelse: String? = null,
        val hjelpeTekst: String? = null,
        val oppfølgingspørmål: List<SporsmalSvar>? = null
    )

    data class MetaInfo(
        val språk: SøknadSpråk = SøknadSpråk.BOKMÅL,
        val hovedOverskrift: String = språk.hovedOverskrift,
        val tittel: String = språk.tittel,
    )

    data class PdfAKrav(val description: String, val subject: String, val author: String)
    data class InfoBlokk(val fødselsnummer: String, val datoFerdigstilt: LocalDateTime) {
        val datoSendt = datoFerdigstilt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
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
