package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.html.HtmlBuilder
import no.nav.dagpenger.soknad.html.InnsendtDokument
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

internal fun lagArkiverbartDokument(innsendSøknad: InnsendtDokument): List<ArkiverbartDokument> {
    return listOf(
        ArkiverbartDokument.netto(HtmlBuilder.lagNettoHtml(innsendSøknad).let { PdfBuilder.lagPdf(it) }),
        ArkiverbartDokument.brutto(
            HtmlBuilder.lagBruttoHtml(innsendSøknad).let { PdfBuilder.lagPdf(it) }
        )
    )
}
