package no.nav.dagpenger.soknad.pdf

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import no.nav.dagpenger.soknad.ArkiverbartDokument
import no.nav.dagpenger.soknad.leggTilUrn

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

    internal suspend fun lagrePdf(søknadUUid: String, arkiverbartDokument: List<ArkiverbartDokument>): List<ArkiverbartDokument> =
        httpKlient.submitFormWithBinaryData(
            url = "$baseUrl/$søknadUUid",
            formData = formData {
                arkiverbartDokument.forEach {
                    append(
                        it.filnavn, it.pdfByteSteam,
                        Headers.build {
                            append(HttpHeaders.ContentType, ContentType.Application.Pdf.toString())
                            append(HttpHeaders.ContentDisposition, "filename=${it.filnavn}")
                        }
                    )
                }
            }
        ).body<List<URNResponse>>().let {
            arkiverbartDokument.leggTilUrn(it)
        }
}

internal data class URNResponse(val filnavn: String, val urn: String)
