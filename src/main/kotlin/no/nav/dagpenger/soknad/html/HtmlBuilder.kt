package no.nav.dagpenger.soknad.html

import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.dom.serialize
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.style
import kotlinx.html.title
import no.nav.dagpenger.soknad.html.HtmlModell.SporsmalSvar
import no.nav.dagpenger.soknad.pdf.PdfBuilder
import java.io.File

internal object HtmlBuilder {
    fun lagHtml(htmlModell: HtmlModell): String {
        val språk = htmlModell.metaInfo.språk
        return createHTMLDocument().html {
            lang = språk.langAtributt
            head {
                title(htmlModell.metaInfo.tittel)
                pdfa(htmlModell.pdfAKrav)
                fontimports()
                style {
                    søknadPdfStyle()
                }
            }
            body {
                h1 {
                    +htmlModell.metaInfo.hovedOverskrift
                }
                div(classes = "infoblokk") {
                    boldSpanP(boldTekst = språk.fødselsnummer, vanligTekst = htmlModell.infoBlokk.fødselsnummer)
                    boldSpanP(boldTekst = språk.datoSendt, vanligTekst = htmlModell.infoBlokk.fødselsnummer)
                }
                htmlModell.seksjoner.forEach { seksjon ->
                    div(classes = "seksjon") {
                        h2 { +seksjon.overskrift }
                        seksjon.spmSvar.forEach { spmDiv(it, språk) }
                    }
                }
            }
        }.serialize(true).xhtmlCompliant()
    }
}

fun main() {
    testHtml.also { generertHtml ->
        print(generertHtml)
        File("soknad.html").writeText(generertHtml)
        PdfBuilder().lagPdf(generertHtml).also {
            File("søknad.pdf").writeBytes(it)
        }
    }
}

internal val spmOgSvarSeksjon = HtmlModell.Seksjon(
    overskrift = "Reel arbeidsøker",
    spmSvar = listOf(
        SporsmalSvar(
            sporsmal = "Kan du jobbe både heltid og deltid?",
            svar = "Ja"
        ),
        SporsmalSvar(
            sporsmal = "Kan du jobbe i hele Norge?",
            svar = "Ja",
        ),
        SporsmalSvar(
            sporsmal = "Kan du ta alle typer arbeid?",
            svar = "Ja",
        ),
        SporsmalSvar(
            sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
            svar = "Ja",
        ),
    )

)

internal val oppfølgingspørmål = listOf(
    SporsmalSvar(
        "Hvorfor ikke?",
        "Fordi sånn kan det være att det er at det er noen ganger at sånn kan det være"
    ),
    SporsmalSvar(
        sporsmal = "Et  annet spørmsål",
        svar = "med et annet svar som også har oppfølging",
        oppfølgingspørmål = listOf(SporsmalSvar("Hvorfor så mye oppfølging?", "Fordi vi følger opp all oppfølginga selvfølgelig"))
    )
)
internal val spmOgSvarMedBarnSeksjon = HtmlModell.Seksjon(
    overskrift = "Reel arbeidsøker med oppfølgingspørsmål", spmSvar = listOf(
        SporsmalSvar(
            sporsmal = "Kan du jobbe både heltid og deltid?",
            svar = "nei",
            oppfølgingspørmål = oppfølgingspørmål
        ),
        SporsmalSvar(
            sporsmal = "Kan du jobbe i hele Norge?",
            svar = "Ja"
        ),
        SporsmalSvar(
            sporsmal = "Kan du ta alle typer arbeid?",
            svar = "Ja"
        ),
        SporsmalSvar(
            sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
            svar = "Ja"
        ),
    )

)
internal val spmOgSvarMedHjelpetekstSeksjon = HtmlModell.Seksjon(
    overskrift = "Seksjon 2",
    spmSvar = listOf(
        SporsmalSvar(
            sporsmal = "Dett er spm 1",
            svar = "svar 1",
            infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
        ),
        SporsmalSvar(
            sporsmal = "Dett er spm 2",
            svar = "svar 2",
            infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
        ),
        SporsmalSvar(
            sporsmal = "Dett er spm 3",
            svar = "svar 3",
            infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
        ),
    )
)

internal val spmOgSvarMedHjelpetekstOgOppfølgingSeksjon = HtmlModell.Seksjon(
    overskrift = "Seksjon 2 med oppfølgingspørmsål",
    spmSvar = listOf(
        SporsmalSvar(
            sporsmal = "Dett er spm 1",
            svar = "svar 1",
            infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
        ),
        SporsmalSvar(
            sporsmal = "Dett er spm 2 som skal ha oppfølgingspørsmål",
            svar = "svar 2",
            infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
            oppfølgingspørmål = oppfølgingspørmål
        ),
        SporsmalSvar(
            sporsmal = "Dett er spm 3",
            svar = "svar 3",
            infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
        ),
    )
)

val testHtml = HtmlBuilder.lagHtml(
    htmlModell = HtmlModell(
        seksjoner = listOf(
            spmOgSvarSeksjon,
            spmOgSvarMedBarnSeksjon,
            spmOgSvarMedHjelpetekstSeksjon,
            spmOgSvarMedHjelpetekstOgOppfølgingSeksjon
        ),
        metaInfo = HtmlModell.MetaInfo(
            hovedOverskrift = "Søknad om dagpenger"
        ),
        pdfAKrav = HtmlModell.PdfAKrav("Søknad om dagpenger", "dagpenger", "NAV"),
        infoBlokk = HtmlModell.InfoBlokk("12345678910", "24.03.2022 11.34")
    )
)