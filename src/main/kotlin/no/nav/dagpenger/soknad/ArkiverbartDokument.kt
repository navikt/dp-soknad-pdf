package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.html.HtmlBuilder
import no.nav.dagpenger.soknad.html.Innsending
import no.nav.dagpenger.soknad.pdf.PdfBuilder

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

internal fun lagArkiverbartDokument(innsending: Innsending): List<ArkiverbartDokument> {
    return listOf(
        ArkiverbartDokument.netto(HtmlBuilder.lagNettoHtml(innsending).let { PdfBuilder.lagPdf(it) }),
        ArkiverbartDokument.brutto(
            HtmlBuilder.lagBruttoHtml(innsending).let { PdfBuilder.lagPdf(it) }
        )
    )
}
