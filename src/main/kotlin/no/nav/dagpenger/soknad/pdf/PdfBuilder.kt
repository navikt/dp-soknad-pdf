package no.nav.dagpenger.soknad.pdf

import no.nav.helse.rapids_rivers.JsonMessage

internal class PdfBuilder(packet: JsonMessage) {

    fun lagPdf(): ByteArray {
        return "Hello world!".toByteArray()
    }
}
