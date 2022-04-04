package no.nav.dagpenger.soknad.pdf

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.streams.*
import java.io.ByteArrayInputStream

class PdfLagring(private val httpKlient: HttpClient = HttpClient()) {

    internal suspend fun lagrePdf(søknadUUid: String, pdf: ByteArray): String {
        /*
        1. http klient
        2. azure ad access token
        3. Lage request
        4.Post request
        */

        val token = "abc"
        return ByteArrayInputStream(pdf).asInput().use {
            httpKlient.post<String> {
                url("http://dp-mellomlagring/v1/azuread/vedlegg/$søknadUUid")
                header("Authorization", "Bearer $token")
                body = MultiPartFormDataContent(
                    formData {
                        appendInput("soknad", Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=soknad.pdf")
                        }) {
                            it
                        }
                    }
                )
            }
        }
    }
}
