package no.nav.dagpenger.innsending

import no.nav.dagpenger.innsending.html.InnsendingSupplier
import no.nav.dagpenger.innsending.pdf.PdfLagring
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
            pdfLagring = PdfLagring(
                baseUrl = Configuration.dpMellomlagringBaseUrl, tokenSupplier = Configuration.mellomlagringTokenSupplier
            ),
            innsendingSupplier = InnsendingSupplier(Configuration.dpSoknadUrl, Configuration.soknadTokenSupplier)::hentSoknad,
        )
    }

    fun start() = rapidsConnection.start()
}
