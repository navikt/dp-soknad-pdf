package no.nav.dagpenger.soknad.pdf

import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

fun main() {
    App.start()
}

internal object App : RapidsConnection.StatusListener {
    private val rapidsConnection = RapidApplication.create(mapOf())

    init {
        rapidsConnection.register(this)
    }
    fun start() = rapidsConnection.start()
}
