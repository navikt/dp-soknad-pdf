package no.nav.dagpenger.soknad.html

import no.nav.dagpenger.soknad.html.SøknadSpråk.BOKMÅL

internal data class HtmlModell(val seksjoner: List<Seksjon>, val metaInfo:MetaInfo) {
    data class Seksjon(val overskrift: String, val spmSvar: List<SporsmalSvar>)
    data class SporsmalSvar(val sporsmal: String, val svar: String)
    data class MetaInfo(val hovedOverskrift: String, val tittel: String = hovedOverskrift, val språk: SøknadSpråk= BOKMÅL)
}

internal enum class SøknadSpråk(val langAtributt:String, val svar: String) {
    BOKMÅL("no","Svar")
}
