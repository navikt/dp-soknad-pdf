package no.nav.dagpenger.innsending.html

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
import mu.KotlinLogging
import no.nav.dagpenger.innsending.serder.JsonHtmlMapper
import java.util.UUID

val logger = KotlinLogging.logger {}
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
        }
    }

    suspend fun hentSoknad(id: UUID, språk: Innsending.InnsendingsSpråk): Innsending {
        return withContext(Dispatchers.IO) {
            val fakta = async {
                httpKlient.get("$dpSoknadBaseUrl/$id/ferdigstilt/fakta").bodyAsText()
            }
            logger.info("mottok søknaddata", fakta)
            val tekst = async {
                httpKlient.get("$dpSoknadBaseUrl/$id/ferdigstilt/tekst").bodyAsText()
            }

            logger.info(" mottok søknadstekst ", tekst)

            JsonHtmlMapper(innsendingsData = fakta.await(), tekst = tekst.await(), språk = språk).parse()
        }
    }
}
