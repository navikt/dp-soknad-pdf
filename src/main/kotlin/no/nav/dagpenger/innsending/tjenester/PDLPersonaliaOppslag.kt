package no.nav.dagpenger.innsending.tjenester

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.jackson.jackson
import no.nav.dagpenger.pdl.createPersonOppslag

internal interface PersonaliaOppslag {
    suspend fun hentPerson(ident: String): Personalia
}

internal class PDLPersonaliaOppslag(pdlUrl: String, private val tokenProvider: () -> String) : PersonaliaOppslag {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                registerModules(JavaTimeModule())
            }
        }
        defaultRequest {
            header("TEMA", "DAG")
        }
    }

    private val personOppslag = createPersonOppslag(pdlUrl, httpClient)

    override suspend fun hentPerson(ident: String): Personalia {
        return personOppslag.hentPerson(
            ident,
            mapOf(
                HttpHeaders.Authorization to "Bearer ${tokenProvider()}"
            )
        ).let {
            Personalia(
                navn = Personalia.Navn(
                    forNavn = it.fornavn,
                    mellomNavn = it.mellomnavn,
                    etterNavn = it.etternavn
                )
            )
        }
    }
}

internal data class Personalia(
    val navn: Navn,
) {
    data class Navn(
        val forNavn: String,
        val mellomNavn: String?,
        val etterNavn: String,
    ) {
        val formatertNavn: String = listOfNotNull(forNavn, mellomNavn, etterNavn).joinToString(" ")
    }
}
