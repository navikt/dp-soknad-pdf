package no.nav.dagpenger.soknad

internal class ArkiverbartDokument private constructor(val variant: DokumentVariant, val pdf: ByteArray) {
    internal val filnavn = "${variant.name.lowercase()}.pdf"

    companion object {
        internal fun netto(pdf: ByteArray) = ArkiverbartDokument(DokumentVariant.NETTO, pdf)
        internal fun brutto(pdf: ByteArray) = ArkiverbartDokument(DokumentVariant.BRUTTO, pdf)
    }

    enum class DokumentVariant {
        NETTO, BRUTTO
    }
}

internal class LagretDokument(
    val urn: String,
    val variant: ArkiverbartDokument.DokumentVariant,
    val filnavn: String
)