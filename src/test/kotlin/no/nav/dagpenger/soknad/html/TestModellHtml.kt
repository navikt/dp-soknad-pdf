package no.nav.dagpenger.soknad.html

import no.nav.dagpenger.soknad.html.InnsendtSøknad.EnkeltSvar
import no.nav.dagpenger.soknad.html.InnsendtSøknad.SpørmsålOgSvarGruppe
import no.nav.dagpenger.soknad.html.InnsendtSøknad.SvarAlternativ
import java.time.LocalDateTime

object TestModellHtml {
    private val svarAlternativ = listOf(
        SvarAlternativ(
            tekst = "Ett svaralternativ",
            tilleggsinformasjon = null
        ),
        SvarAlternativ(
            tekst = "ja",
            tilleggsinformasjon = InnsendtSøknad.InfoTekst(
                "En tittel",
                "Med noe tekst som kan være like lang som hjelpetekste vil jeg tro",
                type = InnsendtSøknad.Infotype.ADVARSEL
            )
        ),
        SvarAlternativ(
            tekst = "nei",
            tilleggsinformasjon = InnsendtSøknad.InfoTekst(
                tittel = null,
                tekst = "Med noe tekst som kan være like lang som hjelpetekste vil jeg tro. Og forhåentligvis har mindre skrivefeil",
                type = InnsendtSøknad.Infotype.ADVARSEL
            )
        )

    )
    private val spmOgSvarSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Reel arbeidsøker",
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = EnkeltSvar("Ja")
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = EnkeltSvar("Ja"),
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = EnkeltSvar("Ja"),
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = EnkeltSvar("Ja"),
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Funker flersvar?",
                svar = InnsendtSøknad.FlerSvar(svarAlternativ),
            ),
        )
    )

    private val enkeltSvarGruppe = listOf(
        InnsendtSøknad.SporsmalSvar(
            "Hvorfor ikke?",
            EnkeltSvar("Fordi sånn kan det være att det er at det er noen ganger at sånn kan det være")
        ),
        InnsendtSøknad.SporsmalSvar(
            sporsmal = "Et  annet spørmsål",
            svar = EnkeltSvar("med et annet svar som også har oppfølging"),
            oppfølgingspørmål = listOf(
                SpørmsålOgSvarGruppe(
                    listOf(
                        InnsendtSøknad.SporsmalSvar(
                            "Hvorfor så mye oppfølging?",
                            EnkeltSvar("Fordi vi følger opp all oppfølginga selvfølgelig")
                        )
                    )
                )
            )
        )
    )
    private val oppfølgingspørmål = listOf(
        SpørmsålOgSvarGruppe(enkeltSvarGruppe)
    )
    private val spmOgSvarMedBarnSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Reel arbeidsøker med oppfølgingspørsmål",
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = EnkeltSvar("nei"),
                oppfølgingspørmål = oppfølgingspørmål
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = EnkeltSvar("Ja")
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = EnkeltSvar("Ja")
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = EnkeltSvar("Ja")
            ),
        )
    )

    private
    val spmOgSvarMedHjelpetekstSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Seksjon 2",
        hjelpetekst = InnsendtSøknad.Hjelpetekst("Hjelpetekst som er hjelpetekst som hjelper"),
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = EnkeltSvar("svar 1"),
                hjelpetekst = InnsendtSøknad.Hjelpetekst(
                    tittel = "Tittel til en hjelpetekst",
                    tekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."
                )
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 2",
                svar = EnkeltSvar("svar 2"),
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = EnkeltSvar("svar 3"),
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
        )
    )

    private
    val spmOgSvarMedHjelpetekstOgOppfølgingSeksjon = InnsendtSøknad.Seksjon(
        overskrift = "Seksjon 2 med oppfølgingspørmsål",
        spmSvar = listOf(
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = EnkeltSvar("svar 1"),
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 2 som skal ha oppfølgingspørsmål",
                svar = EnkeltSvar("svar 2"),
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."),
                oppfølgingspørmål = oppfølgingspørmål
            ),
            InnsendtSøknad.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = EnkeltSvar("svar 3"),
                beskrivelse = "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.",
                hjelpetekst = InnsendtSøknad.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."),
            ),
        ),
        beskrivelse = "En description er en beskrivelse av en egen elefant"
    )

    internal
    val innsendtSøknad = InnsendtSøknad(
        seksjoner = listOf(
            spmOgSvarSeksjon,
            spmOgSvarMedBarnSeksjon,
            spmOgSvarMedHjelpetekstSeksjon,
            spmOgSvarMedHjelpetekstOgOppfølgingSeksjon
        ),
        generellTekst = InnsendtSøknad.GenerellTekst(
            hovedOverskrift = "Søknad om dagpenger",
            tittel = "Søknad om dagpenger",
            svar = "Svar",
            datoSendt = "Dato sendt",
            fnr = "fødselsnummer"
        ),
        språk = InnsendtSøknad.DokumentSpråk.BOKMÅL
    ).apply {
        infoBlokk = InnsendtSøknad.InfoBlokk("12345678910", LocalDateTime.now())
    }
}
