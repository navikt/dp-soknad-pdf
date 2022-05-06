package no.nav.dagpenger.soknad.html

import no.nav.dagpenger.soknad.html.SøknadSpråk.BOKMÅL

internal data class HtmlModell(val seksjoner: List<Seksjon>, val metaInfo:MetaInfo,val pdfAKrav: PdfAKrav, val infoBlokk:InfoBlokk) {
    data class Seksjon(val overskrift: String, val spmSvar: List<SporsmalSvar>)
    data class SporsmalSvar(val sporsmal: String, val svar: String)
    data class MetaInfo(val hovedOverskrift: String, val tittel: String = hovedOverskrift, val språk: SøknadSpråk= BOKMÅL)
    data class PdfAKrav(val description: String)
    data class InfoBlokk(val fødselsnummer: String,val datoSendt: String){
    }
}

internal enum class SøknadSpråk(val langAtributt:String, val svar: String, val fødselsnummer: String,val datoSendt: String) {
    BOKMÅL("no","Svar", "Fødselsnummer","Dato sendt")
}
