package no.nav.dagpenger.innsending

import no.nav.dagpenger.innsending.html.InnsendingSupplier
import no.nav.dagpenger.innsending.pdf.PdfLagring
import no.nav.dagpenger.innsending.tjenester.PDLPersonaliaOppslag
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

fun main() {
    App.start()
}

internal object App : RapidsConnection.StatusListener {
    private val rapidsConnection = RapidApplication.create(Configuration.config)

    init {
        val personaliaOppslag = PDLPersonaliaOppslag(
            pdlUrl = Configuration.pdlApiUrl,
            tokenProvider = Configuration.pdlTokenSupplier
        )
        rapidsConnection.register(this)
        NyDialogPdfBehovLøser(
            rapidsConnection = rapidsConnection,
            pdfLagring = PdfLagring(
                baseUrl = Configuration.dpMellomlagringBaseUrl, tokenSupplier = Configuration.mellomlagringTokenSupplier
            ),
            innsendingSupplier = InnsendingSupplier(
                dpSoknadBaseUrl = Configuration.dpSoknadUrl,
                tokenSupplier = Configuration.soknadTokenSupplier,
                personaliOppslag = personaliaOppslag
            )
        )
        EttersendingPdfBehovLøser(
            rapidsConnection = rapidsConnection,
            pdfLagring = PdfLagring(
                baseUrl = Configuration.dpMellomlagringBaseUrl, tokenSupplier = Configuration.mellomlagringTokenSupplier
            ),
            innsendingSupplier = InnsendingSupplier(
                dpSoknadBaseUrl = Configuration.dpSoknadUrl,
                tokenSupplier = Configuration.soknadTokenSupplier,
                personaliOppslag = personaliaOppslag
            )::hentEttersending
        )
    }

    fun start() = rapidsConnection.start()
}
