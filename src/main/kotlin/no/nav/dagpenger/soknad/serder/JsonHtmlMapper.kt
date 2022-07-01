package no.nav.dagpenger.soknad.serder

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.soknad.LandOppslag
import no.nav.dagpenger.soknad.html.InnsendtDokument
import no.nav.dagpenger.soknad.html.InnsendtDokument.EnkeltSvar
import no.nav.dagpenger.soknad.html.InnsendtDokument.Svar
import no.nav.helse.rapids_rivers.asLocalDate
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal class JsonHtmlMapper(
    private val søknadsData: String,
    tekst: String,
    private val språk: InnsendtDokument.DokumentSpråk = InnsendtDokument.DokumentSpråk.BOKMÅL,
) {
    private val oppslag = Oppslag(tekst)
    private val objectMapper = jacksonObjectMapper()

    private fun parse(søknadsData: String): List<InnsendtDokument.Seksjon> {
        return objectMapper.readTree(søknadsData)["seksjoner"].map {
            val tekstObjekt = oppslag.lookup(it["beskrivendeId"].asText()) as Oppslag.TekstObjekt.SeksjonTekstObjekt
            InnsendtDokument.Seksjon(
                overskrift = tekstObjekt.title,
                beskrivelse = tekstObjekt.description,
                hjelpetekst = tekstObjekt.helpText(),
                spmSvar = it.fakta()
            )
        }
    }

    private fun JsonNode.svar(): Svar {
        return when (val type = this["type"].asText()) {
            "tekst" -> EnkeltSvar(this["svar"].asText())
            "double" -> EnkeltSvar(this["svar"].asText())
            "int" -> EnkeltSvar(this["svar"].asText())
            "boolean" -> EnkeltSvar(språk.boolean(this["svar"].asBoolean()))
            "localdate" -> EnkeltSvar(this["svar"].asLocalDate().dagMånedÅr())
            "periode" -> EnkeltSvar(
                "${
                this["svar"]["fom"].asLocalDate().dagMånedÅr()
                } - ${this["svar"]["tom"].asLocalDate().dagMånedÅr()}"
            )
            "generator" -> InnsendtDokument.IngenSvar
            "envalg" -> EnkeltSvar((oppslag.lookup(this["svar"].asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt).text)
            "flervalg" -> InnsendtDokument.FlerSvar(this.flerValg())
            "land" -> EnkeltSvar(LandOppslag.hentLand(språk, this["svar"].asText()))
            "dokument" -> EnkeltSvar(this.dokumentTekst())
            else -> throw IllegalArgumentException("Ukjent faktumtype $type")
        }
    }

    private fun JsonNode.flerValg(): List<InnsendtDokument.SvarAlternativ> {
        return when (this["type"].asText()) {
            "flervalg" -> this["svar"].toList().map { jsonNode ->
                val jsonAlternativ =
                    (oppslag.lookup(jsonNode.asText()) as Oppslag.TekstObjekt.SvaralternativTekstObjekt)
                InnsendtDokument.SvarAlternativ(
                    jsonAlternativ.text,
                    alertText(jsonAlternativ)
                )
            }
            else -> emptyList()
        }
    }

    private fun alertText(tekstObjekt: Oppslag.TekstObjekt.SvaralternativTekstObjekt): InnsendtDokument.InfoTekst? {
        return tekstObjekt.alertText?.let { alerttext ->
            InnsendtDokument.Infotype.fraSanityJson(typenøkkel = alerttext.type)?.let { infotype ->
                InnsendtDokument.InfoTekst(
                    tittel = alerttext.title,
                    tekst = alerttext.body,
                    type = infotype
                )
            }
        }
    }

    private fun JsonNode.generatorfakta(): List<InnsendtDokument.SpørmsålOgSvarGruppe> {
        return when (this["type"].asText()) {
            "generator" -> this["svar"].toList().map { liste ->
                InnsendtDokument.SpørmsålOgSvarGruppe(
                    liste.toList().map { node ->
                        val tekstObjekt =
                            oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
                        InnsendtDokument.SporsmalSvar(
                            sporsmal = tekstObjekt.text,
                            svar = node.svar(),
                            beskrivelse = tekstObjekt.description,
                            hjelpetekst = tekstObjekt.helpText(),
                            oppfølgingspørmål = node.generatorfakta(),

                        )
                    }
                )
            }

            else -> emptyList()
        }
    }

    private fun JsonNode.fakta(): List<InnsendtDokument.SporsmalSvar> {
        return this["fakta"].map { node ->
            val tekstObjekt = oppslag.lookup(node["beskrivendeId"].asText()) as Oppslag.TekstObjekt.FaktaTekstObjekt
            InnsendtDokument.SporsmalSvar(
                sporsmal = tekstObjekt.text,
                svar = node.svar(),
                beskrivelse = tekstObjekt.description,
                hjelpetekst = tekstObjekt.helpText(),
                oppfølgingspørmål = node.generatorfakta(),
            )
        }
    }

    fun parse(): InnsendtDokument {
        return InnsendtDokument(
            seksjoner = parse(søknadsData),
            generellTekst = oppslag.generellTekst(),
            språk = språk
        )
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

private fun Oppslag.TekstObjekt.helpText(): InnsendtDokument.Hjelpetekst? {
    return this.helpText?.let { InnsendtDokument.Hjelpetekst(it.body, it.title) }
}
