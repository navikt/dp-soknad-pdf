package no.nav.dagpenger.innsending.html

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.nav.dagpenger.innsending.serder.JsonHtmlMapper
import java.util.UUID

internal class InnsendingSupplier(
    private val dpSoknadBaseUrl: String,
    tokenSupplier: () -> String,
    engine: HttpClientEngine = CIO.create()
) {
    private val httpKlient: HttpClient = HttpClient(engine) {
        defaultRequest {
            header("Authorization", "Bearer ${tokenSupplier.invoke()}")
        }
        install(ContentNegotiation) {
            jackson { }
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun hentSoknad(id: UUID, språk: Innsending.InnsendingsSpråk): Innsending {
        return withContext(Dispatchers.IO) {
            val fakta = async { hentFakta(id) }
            val tekst = async { hentTekst(id) }
            val dokumentasjonsKrav = async { hentDokumentasjonKrav(id) }
            JsonHtmlMapper(
                innsendingsData = fakta.await(),
                dokumentasjonKrav = dokumentasjonsKrav.await(),
                tekst = tekst.await(),
                språk = språk
            ).parse()
        }
    }

    suspend fun hentEttersending(
        id: UUID,
        språk: Innsending.InnsendingsSpråk,
        innsendingCopyFunc: Innsending.() -> Innsending = { this }
    ): Innsending {
        return withContext(Dispatchers.IO) {
            val tekst = async { hentTekst(id) }
            val dokumentasjonsKrav = async { hentDokumentasjonKrav(id) }
            JsonHtmlMapper(
                innsendingsData = null,
                dokumentasjonKrav = dokumentasjonsKrav.await(),
                tekst = tekst.await(),
                språk = språk
            ).parseEttersending().innsendingCopyFunc()
        }
    }

    internal suspend fun hentFakta(id: UUID): String {
        return httpKlient.get("$dpSoknadBaseUrl/$id/ferdigstilt/fakta").bodyAsText()
    }

    internal suspend fun hentTekst(id: UUID): String {
        return httpKlient.get("$dpSoknadBaseUrl/$id/ferdigstilt/tekst").bodyAsText()
    }

    internal suspend fun hentDokumentasjonKrav(id: UUID): String {
        return httpKlient.get("$dpSoknadBaseUrl/soknad/$id/dokumentasjonskrav").bodyAsText()
    }
}
