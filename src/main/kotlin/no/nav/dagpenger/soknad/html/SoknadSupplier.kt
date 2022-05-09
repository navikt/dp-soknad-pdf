package no.nav.dagpenger.soknad.html

import com.fasterxml.jackson.databind.JsonNode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.serialization.jackson.jackson
import java.time.LocalDate
import java.util.UUID

internal class SoknadSupplier(
    private val dpSoknadUrl: String,
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
    }

    suspend fun hentSoknad(id: UUID): HtmlModell {
        return httpKlient.get() {
            url("$dpSoknadUrl/$id")
        }.body<JsonNode>().let(Mapper::toModel)
    }
}

internal object Mapper {
    // todo fixme
    fun toModel(json: JsonNode): HtmlModell {
        return HtmlModell(
            seksjoner = listOf(),
            metaInfo = HtmlModell.MetaInfo(hovedOverskrift = "", tittel = ""),
            pdfAKrav = HtmlModell.PdfAKrav(description = "todo", subject = "todo", author = "todo"),
            infoBlokk = HtmlModell.InfoBlokk(fødselsnummer = "todo", datoSendt = LocalDate.now().toString())
        )
    }
}
