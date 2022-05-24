package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.html.HtmlBuilder
import no.nav.dagpenger.soknad.html.HtmlModell
import no.nav.dagpenger.soknad.pdf.PdfBuilder

internal object Hubba {
    fun hubba(modell: HtmlModell): List<ArkiverbartDokument> {
        return listOf(
            ArkiverbartDokument.netto(HtmlBuilder.lagNettoHtml(modell).let { PdfBuilder.lagPdf(it) }),
            ArkiverbartDokument.brutto(
                HtmlBuilder.lagBruttoHtml(modell).let { PdfBuilder.lagPdf(it) }
            )
        )
    }
}
