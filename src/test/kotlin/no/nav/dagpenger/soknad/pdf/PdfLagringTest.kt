package no.nav.dagpenger.soknad.pdf

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
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
                content = """{"urn":"urn:vedlegg:id/soknad.pdf"}""",
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        runBlocking {
            val urn = PdfLagring(
                baseUrl = "http://dp-mellomlagring/v1/azuread/ve",
                tokenSupplier = { "token" },
                engine = mockEngine
            ).lagrePdf("uuud", "".toByteArray())

            assertEquals(urn.urn, """urn:vedlegg:id/soknad.pdf""")
        }
    }
}
