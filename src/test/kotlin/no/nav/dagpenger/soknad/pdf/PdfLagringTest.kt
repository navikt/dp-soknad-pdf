package no.nav.dagpenger.soknad.pdf

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.soknad.ArkiverbartDokument
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class PdfLagringTest {

    @Test
    fun `Sender pdf til mellomlagring`() {
        val mockEngine = MockEngine { request ->

            assertEquals(HttpMethod.Post, request.method)
            assertNotNull(request.headers["Authorization"])
            assertEquals(request.headers["Authorization"], "Bearer token")

            respond(
                //language=JSON
                content = """[{"filnavn":"netto.pdf","urn":"urn:vedlegg:id/netto.pdf"},{"filnavn":"brutto.pdf","urn":"urn:vedlegg:id/brutto.pdf"}]""",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        runBlocking {
            val dokumentliste = PdfLagring(
                baseUrl = "http://dp-mellomlagring/v1/azuread/ve",
                tokenSupplier = { "token" },
                engine = mockEngine
            ).lagrePdf(
                "uuid",
                listOf(
                    ArkiverbartDokument.netto("<!DOCTYPE html>".toByteArray()),
                    ArkiverbartDokument.brutto("<!DOCTYPE html>".toByteArray())
                )
            )
            dokumentliste.single { it.variant == ArkiverbartDokument.DokumentVariant.BRUTTO }.also {
                assertEquals("brutto.pdf", it.filnavn)
                assertEquals("urn:vedlegg:id/brutto.pdf", it.urn)
            }
            dokumentliste.single { it.variant == ArkiverbartDokument.DokumentVariant.NETTO }.also {
                assertEquals("netto.pdf", it.filnavn)
                assertEquals("urn:vedlegg:id/netto.pdf", it.urn)
            }
        }
    }
}
