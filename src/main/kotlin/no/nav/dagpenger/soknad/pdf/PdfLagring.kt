package no.nav.dagpenger.soknad.pdf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import io.ktor.utils.io.streams.asInput
import java.io.ByteArrayInputStream

class PdfLagring(
    private val baseUrl: String,
    tokenSupplier: () -> String,
    engine: HttpClientEngine = CIO.create(),
) {

    private val httpKlient: HttpClient = HttpClient(engine) {
        defaultRequest {
            header("Authorization", "Bearer ${tokenSupplier.invoke()}")
        }
        install(ContentNegotiation) {
            jackson { }
        }
    }

    internal suspend fun lagrePdf(søknadUUid: String, pdf: ByteArray): URNResponse {
        return ByteArrayInputStream(pdf).asInput().use { input ->
            httpKlient.post("$baseUrl/$søknadUUid") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            appendInput("soknad") { input }
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=soknad.pdf")
                            }
                        }
                    )
                )
            }.body()
        }
    }
}

internal data class URNResponse(val urn: String)
