package no.nav.dagpenger.soknad

internal class ArkiverbartDokument private constructor(val variant: DokumentVariant) {
    val filnavn = "${variant.name.lowercase()}.pdf"
    private lateinit var htmlModell: String
    private lateinit var urn: String

    internal fun htmlModell(model: String) {
        require(model.startsWith("<!DOCTYPE html>")) { "modell-string må inneholde HTML" }
        this.htmlModell = model
    }

    companion object {
        internal fun netto(html: String) = ArkiverbartDokument(DokumentVariant.NETTO).apply { htmlModell(html) }
        internal fun brutto(html: String) = ArkiverbartDokument(DokumentVariant.BRUTTO).apply { htmlModell(html) }
    }

    enum class DokumentVariant {
        NETTO, BRUTTO
    }
}