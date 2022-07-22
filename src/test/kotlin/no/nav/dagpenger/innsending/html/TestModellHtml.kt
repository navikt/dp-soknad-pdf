package no.nav.dagpenger.innsending.html

import no.nav.dagpenger.innsending.html.Innsending.EnkeltSvar
import no.nav.dagpenger.innsending.html.Innsending.SpørmsålOgSvarGruppe
import no.nav.dagpenger.innsending.html.Innsending.SvarAlternativ
import java.time.LocalDateTime

object TestModellHtml {
    private val svarAlternativ = listOf(
        SvarAlternativ(
            tekst = "Ett svaralternativ",
            tilleggsinformasjon = null
        ),
        SvarAlternativ(
            tekst = "ja",
            tilleggsinformasjon = Innsending.InfoTekst(
                "En tittel",
                "Med noe tekst som kan være like lang som hjelpetekste vil jeg tro",
                type = Innsending.Infotype.ADVARSEL
            )
        ),
        SvarAlternativ(
            tekst = "nei",
            tilleggsinformasjon = Innsending.InfoTekst(
                tittel = null,
                tekst = "Med noe tekst som kan være like lang som hjelpetekste vil jeg tro. Og forhåentligvis har mindre skrivefeil",
                type = Innsending.Infotype.ADVARSEL
            )
        )

    )
    private val spmOgSvarSeksjon = Innsending.Seksjon(
        overskrift = "Reel arbeidsøker",
        spmSvar = listOf(
            Innsending.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = EnkeltSvar("Ja")
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = EnkeltSvar("Ja"),
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = EnkeltSvar("Ja"),
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = EnkeltSvar("Ja"),
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Funker flersvar?",
                svar = Innsending.FlerSvar(svarAlternativ),
            ),
        )
    )

    private val enkeltSvarGruppe = listOf(
        Innsending.SporsmalSvar(
            "Hvorfor ikke?",
            EnkeltSvar("Fordi sånn kan det være att det er at det er noen ganger at sånn kan det være")
        ),
        Innsending.SporsmalSvar(
            sporsmal = "Et  annet spørmsål",
            svar = EnkeltSvar("med et annet svar som også har oppfølging"),
            oppfølgingspørmål = listOf(
                SpørmsålOgSvarGruppe(
                    listOf(
                        Innsending.SporsmalSvar(
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
    private val spmOgSvarMedBarnSeksjon = Innsending.Seksjon(
        overskrift = "Reel arbeidsøker med oppfølgingspørsmål",
        spmSvar = listOf(
            Innsending.SporsmalSvar(
                sporsmal = "Kan du jobbe både heltid og deltid?",
                svar = EnkeltSvar("nei"),
                oppfølgingspørmål = oppfølgingspørmål
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Kan du jobbe i hele Norge?",
                svar = EnkeltSvar("Ja")
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Kan du ta alle typer arbeid?",
                svar = EnkeltSvar("Ja")
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Er du villig til å bytte yrke eller gå ned i lønn?",
                svar = EnkeltSvar("Ja")
            ),
        )
    )

    private
    val spmOgSvarMedHjelpetekstSeksjon = Innsending.Seksjon(
        overskrift = "Seksjon 2",
        hjelpetekst = Innsending.Hjelpetekst("Hjelpetekst som er hjelpetekst som hjelper"),
        spmSvar = listOf(
            Innsending.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = EnkeltSvar("svar 1"),
                hjelpetekst = Innsending.Hjelpetekst(
                    tittel = "Tittel til en hjelpetekst",
                    tekst = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."
                )
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Dett er spm 2",
                svar = EnkeltSvar("svar 2"),
                beskrivelse = Innsending.UnsafeHtml("<p>Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.</p>"),
                hjelpetekst = Innsending.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = EnkeltSvar("svar 3"),
                beskrivelse = Innsending.UnsafeHtml("<p>Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.</p>"),
                hjelpetekst = Innsending.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.")
            ),
        )
    )

    private
    val spmOgSvarMedHjelpetekstOgOppfølgingSeksjon = Innsending.Seksjon(
        overskrift = "Seksjon 2 med oppfølgingspørmsål",
        spmSvar = listOf(
            Innsending.SporsmalSvar(
                sporsmal = "Dett er spm 1",
                svar = EnkeltSvar("svar 1"),
                beskrivelse = Innsending.UnsafeHtml("""<em>Contrary to popular belief</em>, Lorem Ipsum is not simply random text. <a src="https://roots.lorem/ipsum?woot=ja">It has roots</a> in a piece of classical Latin literature from 45 BC, making it over 2000 years old."""),
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Dett er spm 2 som skal ha oppfølgingspørsmål",
                svar = EnkeltSvar("svar 2"),
                beskrivelse = Innsending.UnsafeHtml("<p>Contrary to popular belief, Lorem Ipsum is not <em>simply random text</em>. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.</p>"),
                hjelpetekst = Innsending.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."),
                oppfølgingspørmål = oppfølgingspørmål
            ),
            Innsending.SporsmalSvar(
                sporsmal = "Dett er spm 3",
                svar = EnkeltSvar("svar 3"),
                beskrivelse = Innsending.UnsafeHtml("<p>Contrary to <strong>popular belief</strong>, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old.</p>"),
                hjelpetekst = Innsending.Hjelpetekst("Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32."),
            ),
        ),
        beskrivelse = Innsending.UnsafeHtml("<p>En description er en beskrivelse av en egen elefant</p>")
    )

    internal
    val innsending = Innsending(
        seksjoner = listOf(
            spmOgSvarSeksjon,
            spmOgSvarMedBarnSeksjon,
            spmOgSvarMedHjelpetekstSeksjon,
            spmOgSvarMedHjelpetekstOgOppfølgingSeksjon
        ),
        generellTekst = Innsending.GenerellTekst(
            hovedOverskrift = "Søknad om dagpenger",
            tittel = "Søknad om dagpenger",
            svar = "Svar",
            datoSendt = "Dato sendt",
            fnr = "fødselsnummer"
        ),
        språk = Innsending.InnsendingsSpråk.BOKMÅL,
        pdfAMetaTagger = Innsending.DefaultPdfAMetaTagger
    ).apply {
        infoBlokk = Innsending.InfoBlokk("12345678910", LocalDateTime.now())
    }
}
