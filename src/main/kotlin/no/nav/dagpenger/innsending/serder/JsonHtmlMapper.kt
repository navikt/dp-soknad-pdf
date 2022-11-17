package no.nav.dagpenger.innsending.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import mu.KotlinLogging
import no.nav.dagpenger.innsending.LandOppslag
import no.nav.dagpenger.innsending.html.Innsending
import no.nav.dagpenger.innsending.html.Innsending.EnkeltSvar
import no.nav.dagpenger.innsending.html.Innsending.Svar
import no.nav.dagpenger.innsending.html.InnsendingSupplier
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.DokumentkravTekstObjekt
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.FaktaTekstObjekt
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.SeksjonTekstObjekt
import no.nav.dagpenger.innsending.serder.Oppslag.TekstObjekt.SvaralternativTekstObjekt
import no.nav.helse.rapids_rivers.asLocalDate
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val sikkerlogg = KotlinLogging.logger("tjenestekall")
private val logger = KotlinLogging.logger {}

internal class JsonHtmlMapper(
    private val innsendingsData: String?,
    private val dokumentasjonKrav: String,
    tekst: String,
    private val språk: Innsending.InnsendingsSpråk = Innsending.InnsendingsSpråk.BOKMÅL,
) {
    private val oppslag = Oppslag(tekst)
    private val objectMapper = jacksonObjectMapper()

    private fun parse(innsendingsData: String?): List<Innsending.Seksjon> {
        return innsendingsData?.let {
            objectMapper.readTree(innsendingsData)["seksjoner"].map {
                val tekstObjekt = oppslag.lookup<SeksjonTekstObjekt>(it["beskrivendeId"].asText())
                Innsending.Seksjon(
                    overskrift = tekstObjekt.title,
                    beskrivelse = tekstObjekt.description?.let { rawHtml -> Innsending.UnsafeHtml(rawHtml.html) },
                    hjelpetekst = tekstObjekt.hjelpetekst(),
                    spmSvar = it.fakta()
                )
            }
        } ?: emptyList()
    }

    private fun parseDokumentkrav(dokumentasjonKrav: String): List<Innsending.DokumentKrav> {
        return objectMapper.readTree(dokumentasjonKrav)["krav"].map { krav ->
            val kravId = krav["id"].asText()
            val kravSvar = krav["beskrivelse"]?.asText()
            val valg = Innsending.DokumentKrav.Valg.fromJson(krav["svar"].asText())
            val tekstObjekt =
                oppslag.lookup<DokumentkravTekstObjekt>(krav["beskrivendeId"].asText())
            when (valg) {
                Innsending.DokumentKrav.Valg.SEND_NAA -> Innsending.Innsendt(
                    kravId = kravId,
                    kravSvar = kravSvar,
                    navn = tekstObjekt.title,
                    beskrivelse = tekstObjekt.description?.let { rawHtml -> Innsending.UnsafeHtml(rawHtml.html) },
                    hjelpetekst = tekstObjekt.hjelpetekst(),
                    valg = valg
                )

                else -> Innsending.IkkeInnsendtNå(
                    kravId = kravId,
                    kravSvar = kravSvar,
                    navn = tekstObjekt.title,
                    begrunnelse = krav["begrunnelse"].asText(),
                    beskrivelse = tekstObjekt.description?.let { rawHtml -> Innsending.UnsafeHtml(rawHtml.html) },
                    hjelpetekst = tekstObjekt.hjelpetekst(),
                    valg = valg
                )
            }
        }
    }

    private fun JsonNode.svar(): Svar {
        return kotlin.runCatching {
            when (val type = this["type"].asText()) {
                "tekst" -> EnkeltSvar(this["svar"].asText())
                "double" -> EnkeltSvar(this["svar"].asText())
                "int" -> EnkeltSvar(this["svar"].asText())
                "boolean" -> Innsending.ValgSvar(this.booleanEnValg())
                "localdate" -> EnkeltSvar(this["svar"].asLocalDate().dagMånedÅr())
                "periode" -> EnkeltSvar(
                    "${
                        this["svar"]["fom"].asLocalDate().dagMånedÅr()
                    } - ${this["svar"]["tom"]?.asLocalDate()?.dagMånedÅr()}"
                )

                "generator" -> Innsending.IngenSvar
                "envalg" -> Innsending.ValgSvar(this.envalg())
                "flervalg" -> Innsending.ValgSvar(this.flerValg())
                "land" -> EnkeltSvar(LandOppslag.hentLand(språk, this["svar"].asText()))
                "dokument" -> EnkeltSvar(this.dokumentTekst())
                else -> throw IllegalArgumentException("Ukjent faktumtype $type")
            }
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                sikkerlogg.error { "Kunne ikke parse json node: $this" }
                throw e
            }
        )
    }

    private fun JsonNode.envalg(): List<Innsending.SvarAlternativ> {
        return oppslag.lookup<SvaralternativTekstObjekt>(this["svar"].asText()).let { jsonAlternativ ->
            listOf(
                Innsending.SvarAlternativ(
                    jsonAlternativ.text,
                    alertText(jsonAlternativ)
                )
            )
        }
    }

    private fun JsonNode.booleanEnValg(): List<Innsending.SvarAlternativ> {
        return oppslag.lookup<SvaralternativTekstObjekt>(this.booleanTextId()).let { jsonAlternativ ->
            listOf(
                Innsending.SvarAlternativ(
                    jsonAlternativ.text,
                    alertText(jsonAlternativ)
                )
            )
        }
    }

    private fun JsonNode.flerValg(): List<Innsending.SvarAlternativ> {
        return when (this["type"].asText()) {
            "flervalg" -> this["svar"].toList().map { jsonNode ->
                val jsonAlternativ = oppslag.lookup<SvaralternativTekstObjekt>(jsonNode.asText())
                Innsending.SvarAlternativ(
                    jsonAlternativ.text,
                    alertText(jsonAlternativ)
                )
            }

            else -> emptyList()
        }
    }

    private fun alertText(tekstObjekt: SvaralternativTekstObjekt): Innsending.InfoTekst? {
        return tekstObjekt.alertText?.let { alerttext ->
            Innsending.Infotype.fraSanityJson(typenøkkel = alerttext.type)?.let { infotype ->
                Innsending.InfoTekst.nyEllerNull(
                    tittel = alerttext.title,
                    unsafeHtmlBody = alerttext.body?.let { Innsending.UnsafeHtml(alerttext.body.html) },
                    type = infotype
                )
            }
        }
    }

    private fun JsonNode.generatorfakta(): List<Innsending.SpørmsålOgSvarGruppe> {
        return when (this["type"].asText()) {
            "generator" -> this["svar"].toList().map { liste ->
                Innsending.SpørmsålOgSvarGruppe(
                    liste.toList().map { node ->
                        val tekstObjekt =
                            oppslag.lookup<FaktaTekstObjekt>(node["beskrivendeId"].asText())
                        Innsending.SporsmalSvar(
                            sporsmal = tekstObjekt.text,
                            svar = node.svar(),
                            beskrivelse = tekstObjekt.description?.let { rawHtml -> Innsending.UnsafeHtml(rawHtml.html) },
                            hjelpetekst = tekstObjekt.hjelpetekst(),
                            oppfølgingspørmål = node.generatorfakta(),

                            )
                    }
                )
            }

            else -> emptyList()
        }
    }

    private fun JsonNode.fakta(): List<Innsending.SporsmalSvar> {
        return this["fakta"].map { node ->
            val tekstObjekt = oppslag.lookup<FaktaTekstObjekt>(node["beskrivendeId"].asText())
            Innsending.SporsmalSvar(
                sporsmal = tekstObjekt.text,
                svar = node.svar(),
                beskrivelse = tekstObjekt.description?.let { rawHtmlString -> Innsending.UnsafeHtml(rawHtmlString.html) },
                hjelpetekst = tekstObjekt.hjelpetekst(),
                oppfølgingspørmål = node.generatorfakta(),
            )
        }
    }

    fun parse(innsendingType: InnsendingSupplier.InnsendingType = InnsendingSupplier.InnsendingType.DAGPENGER): Innsending {
        return Innsending(
            seksjoner = parse(innsendingsData),
            generellTekst = oppslag.generellTekst(innsendingType),
            språk = språk,
            pdfAMetaTagger = oppslag.pdfaMetaTags(),
            dokumentasjonskrav = parseDokumentkrav(dokumentasjonKrav),
            type = innsendingType
        )
    }

    fun parseEttersending(): Innsending {
        return Innsending(
            seksjoner = emptyList(),
            generellTekst = oppslag.generellTekstEttersending(),
            språk = språk,
            pdfAMetaTagger = oppslag.pdfaMetaTags(),
            dokumentasjonskrav = parseDokumentkrav(dokumentasjonKrav)
        )
    }
}

private fun JsonNode.booleanTextId(): String {
    val baseId = "${this["beskrivendeId"].asText()}.svar"
    return when (this["svar"].asBoolean()) {
        false -> "$baseId.nei"
        true -> "$baseId.ja"
    }
}

private fun JsonNode.dokumentTekst(): String {
    val opplastetDato = this["svar"]["lastOppTidsstempel"].asLocalDateTime().dagMånedÅr()
    val filnavn = this["svar"]["urn"].asText().split("/").last()
    return "Du har lastet opp $filnavn den $opplastetDato"
}

private fun LocalDate.dagMånedÅr(): String =
    this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

private fun LocalDateTime.dagMånedÅr(): String =
    this.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

private fun TekstObjekt.hjelpetekst(): Innsending.Hjelpetekst? {
    return this.helpText?.let { oppslag ->
        val unsafeHtml = oppslag.body?.let { Innsending.UnsafeHtml(it.html) }
        Innsending.Hjelpetekst.nyEllerNull(unsafeHtmlBody = unsafeHtml, tittel = oppslag.title)
    }
}
