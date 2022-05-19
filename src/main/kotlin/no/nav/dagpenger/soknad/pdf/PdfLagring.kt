package no.nav.dagpenger.soknad.pdf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import io.ktor.utils.io.streams.asInput

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
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    internal suspend fun lagrePdf(søknadUUid: String, pdfs: Map<String, ByteArray>): List<URNResponse> =
        httpKlient.post("$baseUrl/$søknadUUid") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        pdfs.forEach {
                            appendInput("soknad") { it.value.inputStream().asInput() }
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=${it.key}.pdf") // TODO: fiks filnavn
                                append(HttpHeaders.ContentType, ContentType.Application.Pdf.toString())
                            }
                        }
                    }
                )
            )
        }.body()
}

internal data class URNResponse(val filnavn: String, val urn: String)
