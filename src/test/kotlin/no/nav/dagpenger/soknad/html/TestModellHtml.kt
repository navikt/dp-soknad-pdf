package no.nav.dagpenger.soknad.html

import java.time.LocalDateTime

object TestModellHtml {
    private val spmOgSvarSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Reel arbeidsøker",
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = "Ja"
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = "Ja",
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = "Ja",
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = "Ja",
            ),
        )

    )

    private val oppfølgingspørmål = listOf(
        InnsendtSøknad.SporsmalSvar(
            "Hvorfor ikke?",
            "Fordi sånn kan det være att det er at det er noen ganger at sånn kan det være"
        ),
        InnsendtSøknad.SporsmalSvar(
            sporsmal = "Et  annet spørmsål",
            svar = "med et annet svar som også har oppfølging",
            oppfølgingspørmål = listOf(
                InnsendtSøknad.SporsmalSvar(
                    "Hvorfor så mye oppfølging?",
                    "Fordi vi følger opp all oppfølginga selvfølgelig"
                )
            )
        )
    )

    private val spmOgSvarMedBarnSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Reel arbeidsøker med oppfølgingspørsmål",
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = "nei",
                oppfølgingspørmål = oppfølgingspørmål
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = "Ja"
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = "Ja"
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = "Ja"
            ),
        )
    )

    private val spmOgSvarMedHjelpetekstSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Seksjon 2",
        hjelpetekst = InnsendtSøknad.Hjelpetekst("Hjelpetekst som er hjelpetekst som hjelper"),
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = "svar 1",
                hjelpetekst = InnsendtSøknad.Hjelpetekst(tittel = "Tittel til en hjelpetekst", tekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 2",
                svar = "svar 2",
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = "svar 3",
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
        )
    )

    private val spmOgSvarMedHjelpetekstOgOppfølgingSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Seksjon 2 med oppfølgingspørmsål",
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = "svar 1",
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 2 som skal ha oppfølgingspørsmål",
                svar = "svar 2",
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."),
                oppfølgingspørmål = oppfølgingspørmål
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = "svar 3",
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."),
            ),
        ),
        beskrivelse = "En description er en beskrivelse av en egen elefant"
    )

    internal val innsendtSøknad = InnsendtSøknad(
        seksjoner = listOf(
            spmOgSvarSeksjon,
            spmOgSvarMedBarnSeksjon,
            spmOgSvarMedHjelpetekstSeksjon,
            spmOgSvarMedHjelpetekstOgOppfølgingSeksjon
        ),
        metaInfo = InnsendtSøknad.MetaInfo(
            hovedOverskrift = "Søknad om dagpenger"
        )
    ).apply {
        infoBlokk = InnsendtSøknad.InfoBlokk("12345678910", LocalDateTime.now())
    }
}
