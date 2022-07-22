package no.nav.dagpenger.innsending.html

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal data class Innsending(
    val seksjoner: List<Seksjon>,
    val generellTekst: GenerellTekst,
    val språk: InnsendingsSpråk,
    val pdfAMetaTagger: PdfAMetaTagger
) {

    lateinit var infoBlokk: InfoBlokk

    open class PdfAMetaTagger(
        val description: String,
        val subject: String,
        val author: String,
    )

    object DefaultPdfAMetaTagger :
        PdfAMetaTagger(description = "Søknad om dagpenger", subject = "Dagpenger", author = "NAV Dagpenger")

    data class Seksjon(
        val overskrift: String,
        val beskrivelse: UnsafeHtml? = null,
        val hjelpetekst: Hjelpetekst? = null,
        val spmSvar: List<SporsmalSvar>
    )

    data class SporsmalSvar(
        val sporsmal: String,
        val svar: Svar,
        val beskrivelse: UnsafeHtml? = null,
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

    internal class UnsafeHtml(val innhold: String) {
        private fun injectCssClass(className: String) {}
    }

    enum class InnsendingsSpråk(
        val langAtributt: String
    ) {
        BOKMÅL(
            "no"
        ),
        ENGELSK(
            "en"
        )
    }
}
