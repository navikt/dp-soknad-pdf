package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.html.SoknadSupplier
import no.nav.dagpenger.soknad.pdf.PdfLagring
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
            soknadSupplier = SoknadSupplier(Configuration.dpSoknadUrl, Configuration.soknadTokenSupplier)::hentSoknad,
        )
    }

    fun start() = rapidsConnection.start()
}
