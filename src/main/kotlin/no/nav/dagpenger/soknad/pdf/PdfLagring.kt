package no.nav.dagpenger.soknad.pdf

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }

    internal suspend fun lagrePdf(søknadUUid: String, pdf: ByteArray): URNResponse {
        return ByteArrayInputStream(pdf).asInput().use {
            httpKlient.post<List<URNResponse>>("$baseUrl/$søknadUUid") {
                body = MultiPartFormDataContent(
                    formData {
                        appendInput(
                            "soknad",
                            Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=soknad.pdf")
                            }
                        ) {
                            it
                        }
                    }
                )
            }
        }.first()
    }
}

internal data class URNResponse(val urn: String)
