package no.nav.dagpenger.soknad.pdf

import no.nav.dagpenger.soknad.html.HtmlBuilder
import no.nav.dagpenger.soknad.html.SoknadSupplier
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
            pdfBuilder = PdfBuilder,
            pdfLagring = PdfLagring(
                baseUrl = Configuration.dpMellomlagringBaseUrl, tokenSupplier = Configuration.mellomlagringTokenSupplier
            ),
            htmlBuilder = HtmlBuilder::lagHtml,
            soknadSupplier = SoknadSupplier(Configuration.dpSoknadUrl, Configuration.soknadTokenSupplier)::hentSoknad,
        )
    }

    fun start() = rapidsConnection.start()
}
