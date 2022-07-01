package no.nav.dagpenger.soknad.html

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import no.nav.dagpenger.soknad.serder.JsonHtmlMapper
import java.util.UUID

internal class SoknadSupplier(
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
        }
    }

    suspend fun hentSoknad(id: UUID, språk: InnsendtDokument.DokumentSpråk): InnsendtDokument {
        return withContext(Dispatchers.IO) {
            val fakta = async {
                httpKlient.get("$dpSoknadBaseUrl/$id/ferdigstilt/fakta").bodyAsText()
            }
            val tekst = async {
                httpKlient.get("$dpSoknadBaseUrl/$id/ferdigstilt/tekst").bodyAsText()
            }
            JsonHtmlMapper(søknadsData = fakta.await(), tekst = tekst.await(), språk = språk).parse()
        }
    }
}
