package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.pdf.URNResponse

internal class ArkiverbartDokument private constructor(val variant: DokumentVariant) {
    internal val filnavn = "${variant.name.lowercase()}.pdf"
    private lateinit var htmlModell: String
    internal lateinit var urn: String

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

internal fun List<ArkiverbartDokument>.leggTilUrn(urnResponse: List<URNResponse>): List<ArkiverbartDokument> {
    urnResponse.forEach { response ->
        single { dokument ->
            response.filnavn == dokument.filnavn
        }.apply { urn = response.urn }
    }
    return this
}