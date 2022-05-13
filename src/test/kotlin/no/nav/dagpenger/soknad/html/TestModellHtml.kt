package no.nav.dagpenger.soknad.html

import java.time.LocalDateTime

object TestModellHtml {
    private val spmOgSvarSeksjon = HtmlModell.Seksjon(
        overskrift = "Reel arbeidsøker",
        spmSvar = listOf(
            HtmlModell.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = "Ja"
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = "Ja",
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = "Ja",
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = "Ja",
            ),
        )

    )

    private val oppfølgingspørmål = listOf(
        HtmlModell.SporsmalSvar(
            "Hvorfor ikke?",
            "Fordi sånn kan det være att det er at det er noen ganger at sånn kan det være"
        ),
        HtmlModell.SporsmalSvar(
            sporsmal = "Et  annet spørmsål",
            svar = "med et annet svar som også har oppfølging",
            oppfølgingspørmål = listOf(
                HtmlModell.SporsmalSvar(
                    "Hvorfor så mye oppfølging?",
                    "Fordi vi følger opp all oppfølginga selvfølgelig"
                )
            )
        )
    )
    internal val spmOgSvarMedBarnSeksjon = HtmlModell.Seksjon(
        overskrift = "Reel arbeidsøker med oppfølgingspørsmål",
        spmSvar = listOf(
            HtmlModell.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = "nei",
                oppfølgingspørmål = oppfølgingspørmål
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = "Ja"
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = "Ja"
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = "Ja"
            ),
        )

    )
    internal val spmOgSvarMedHjelpetekstSeksjon = HtmlModell.Seksjon(
        overskrift = "Seksjon 2",
        spmSvar = listOf(
            HtmlModell.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = "svar 1",
                infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Dett er spm 2",
                svar = "svar 2",
                infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = "svar 3",
                infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
            ),
        )
    )

    private val spmOgSvarMedHjelpetekstOgOppfølgingSeksjon = HtmlModell.Seksjon(
        overskrift = "Seksjon 2 med oppfølgingspørmsål",
        spmSvar = listOf(
            HtmlModell.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = "svar 1",
                infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Dett er spm 2 som skal ha oppfølgingspørsmål",
                svar = "svar 2",
                infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
                oppfølgingspørmål = oppfølgingspørmål
            ),
            HtmlModell.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = "svar 3",
                infotekst = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpeTekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.",
            ),
        )
    )

    internal val htmlModell = HtmlModell(
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
        infoBlokk = HtmlModell.InfoBlokk("12345678910", LocalDateTime.now())
    )
}
