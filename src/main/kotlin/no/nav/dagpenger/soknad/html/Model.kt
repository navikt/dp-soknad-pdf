package no.nav.dagpenger.soknad.html

data class HtmlModell(val seksjoner: List<Seksjon>) {
    data class Seksjon(val overskrift: String, val spmSvar: List<SporsmalSvar>)
    data class SporsmalSvar(val sporsmal: String, val svar: String)
}
