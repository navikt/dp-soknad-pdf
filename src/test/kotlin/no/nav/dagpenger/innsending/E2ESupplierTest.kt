package no.nav.dagpenger.innsending

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.Configuration
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.innsending.html.Innsending.InnsendingsSpråk.BOKMÅL
import no.nav.dagpenger.innsending.html.InnsendingSupplier
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Config
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileReader
import java.time.OffsetDateTime
import java.util.UUID

internal class E2ESupplierTest {

    // Hente azuread eller tokenx secret for  app
// jwker.nais.io -> tokenx,  azurerator.nais.io -> azuread
    fun getAuthEnv(app: String, type: String = "jwker.nais.io"): Map<String, String> {
        // file path to your KubeConfig
        val kubeConfigPath = System.getenv("KUBECONFIG")

        // IF this fails do kubectl get pod to aquire credentials
        val client: ApiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(FileReader(kubeConfigPath))).build()
        Configuration.setDefaultApiClient(client)
        return CoreV1Api().listNamespacedSecret(
            "teamdagpenger",
            null,
            null,
            null,
            null,
            "app=$app,type=$type",
            null,
            null,
            null,
            null,
            null
        ).items.also { secrets ->
            secrets.sortByDescending<V1Secret?, OffsetDateTime> { it?.metadata?.creationTimestamp }
        }.first<V1Secret?>()?.data!!.mapValues { e -> String(e.value) }
    }

    fun getAzureAdToken(app: String): String {
        val azureadConfig = OAuth2Config.AzureAd(
            getAuthEnv(app, "azurerator.nais.io")
        )
        val tokenAzureAdClient: CachedOauth2Client by lazy {
            CachedOauth2Client(
                tokenEndpointUrl = azureadConfig.tokenEndpointUrl,
                authType = azureadConfig.clientSecret()
            )
        }

        return tokenAzureAdClient.clientCredentials("api://dev-gcp.teamdagpenger.dp-soknad/.default").accessToken
    }

    @Test
    @Disabled
    fun `hent dokumentasjonskrav`() {
        val ids = listOf<String>(
            "803e5fa5-f235-42db-9590-7e410b3bd661",
            "4a5f6767-3513-448f-a3aa-868eec05e412",
            "9fc062d6-934a-4c06-9c51-54df8d8ee308",
            "963c9444-a682-4c24-8733-7b09febf99b4",
        )
        val innsendingSupplier = InnsendingSupplier(
            dpSoknadBaseUrl = "https://arbeid.dev.nav.no/arbeid/dagpenger/soknadapi",
            tokenSupplier = { getAzureAdToken("dp-behov-soknad-pdf") },
        )
        runBlocking {
            ids.forEach { id ->
                innsendingSupplier.hentSoknad(UUID.fromString(id), BOKMÅL).also {
                    File("./build/tmp/$id.json").writeText(it.toString())
                }
            }
        }
    }
}
