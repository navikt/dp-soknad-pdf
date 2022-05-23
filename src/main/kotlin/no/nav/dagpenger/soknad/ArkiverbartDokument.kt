package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.pdf.URNResponse

internal class ArkiverbartDokument private constructor(val variant: DokumentVariant, html: String) {
    internal val filnavn = "${variant.name.lowercase()}.pdf"
    lateinit var pdfByteSteam: ByteArray
    internal val html: String
    internal lateinit var urn: String

    init {
        require(html.startsWith("<!DOCTYPE html>")) { "stringen må inneholde HTML" }
        this.html = html
    }

    companion object {
        internal fun netto(generertHtml: String) = ArkiverbartDokument(DokumentVariant.NETTO, generertHtml)
        internal fun brutto(generertHtml: String) = ArkiverbartDokument(DokumentVariant.BRUTTO, generertHtml)
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
