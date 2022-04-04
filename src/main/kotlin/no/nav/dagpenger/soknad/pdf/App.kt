package no.nav.dagpenger.soknad.pdf

import no.nav.dagpenger.mottak.tjenester.PdfBehovLøser
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

fun main() {
    App.start()
}

internal object App : RapidsConnection.StatusListener {
    private val rapidsConnection = RapidApplication.create(Configuration.config)

    init {
        rapidsConnection.register(this)
        PdfBehovLøser(
            rapidsConnection = rapidsConnection,
            pdfBuilder = PdfBuilder(),
            pdfLagring = PdfLagring(
                baseUrl = Configuration.dpMellomlagringBaseUrl, tokenSupplier = Configuration.azureAdTokenSupplier
            )
        )
    }

    fun start() = rapidsConnection.start()
}
