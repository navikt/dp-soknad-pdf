package no.nav.dagpenger.soknad.html


internal data class HtmlModell(
    val seksjoner: List<Seksjon>,
    val metaInfo: MetaInfo,
    val pdfAKrav: PdfAKrav,
    val infoBlokk: InfoBlokk
) {
    data class Seksjon(val overskrift: String, val spmSvar: List<SporsmalSvar>)
    data class SporsmalSvar(
        val sporsmal: String,
        val svar: String,
        val infotekst: String? = null,
        val hjelpeTekst: String? = null,
        val oppfølgingspørmål: List<SporsmalSvar>? = null
    )
    data class MetaInfo(
        val hovedOverskrift: String,
        val tittel: String = hovedOverskrift,
        val språk: SøknadSpråk = SøknadSpråk.BOKMÅL
    )
    data class PdfAKrav(val description: String, val subject: String, val author: String)
    data class InfoBlokk(val fødselsnummer: String, val datoSendt: String) {
    }
    enum class SøknadSpråk(
        val langAtributt: String,
        val svar: String,
        val fødselsnummer: String,
        val datoSendt: String
    ) {
        BOKMÅL("no", "Svar", "Fødselsnummer", "Dato sendt")
    }
}

