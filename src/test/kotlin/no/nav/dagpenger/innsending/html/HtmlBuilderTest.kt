package no.nav.dagpenger.innsending.html

import kotlinx.html.BODY
import kotlinx.html.HEAD
import kotlinx.html.div
import kotlinx.html.title
import no.nav.dagpenger.innsending.pdf.PdfBuilder.lagPdf
import org.intellij.lang.annotations.Language
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File

class HtmlBuilderTest {
    @Disabled
    @Test
    fun manuellTest() {
        assertDoesNotThrow {
            HtmlBuilder.lagBruttoHtml(TestModellHtml.innsending).also {
                File("build/tmp/test/søknad.html").writeText(it)
                lagPdf(it).also { generertPdf ->
                    File("build/tmp/test/søknad.pdf").writeBytes(generertPdf)
                }
            }
        }
    }

    @Test
    fun `lager html`() {
        val head: HEAD.() -> Unit = {
            title("Test tittel")
        }

        val body: BODY.() -> Unit = {
            div { +"Test div" }
        }

        val html = HtmlBuilder.lagHtml("no-NB", head, body)

        assertEquals(
            "<html lang=\"no-NB\"><head><title>Test tittel</title></head><body><div>Test div</div></body></html>",
            html,
        )
    }

    @Test
    fun `lager netto html`() {
        HtmlBuilder.lagNettoHtml(TestModellHtml.innsending).also {
            assertEquals(0, antallElementerMedKlassenavn(it, "infotekst"), "Feil antall infotekster")
            assertEquals(0, antallElementerMedKlassenavn(it, "hjelpetekst"), "Feil antall hjelpetekster")
            assertEquals(4, antallElementerMedKlassenavn(it, "seksjon"), "Feil antall seksjoner")
            assertEquals(1, antallElementerMedKlassenavn(it, "dokumentasjon"), "Feil antall dokumentasjon")
            assertEquals(2, antallElementerMedKlassenavn(it, "dokumentasjonkrav"), "Feil antall dokumentasjonskrav")
        }
    }

    @Test
    fun `lager brutto html`() {
        HtmlBuilder.lagBruttoHtml(TestModellHtml.innsending).also {
            assertEquals(6, antallElementerMedKlassenavn(it, "infotekst"), "Feil antall infortekster")
            assertEquals(8, antallElementerMedKlassenavn(it, "hjelpetekst"), "Feil antall hjelpetekster")
            assertEquals(4, antallElementerMedKlassenavn(it, "seksjon"), "Feil antall hjelpetekster")
            assertEquals(1, antallElementerMedKlassenavn(it, "dokumentasjon"), "Feil antall dokumentasjon")
            assertEquals(2, antallElementerMedKlassenavn(it, "dokumentasjonkrav"), "Feil antall dokumentasjonskrav")
        }
    }
}

private fun antallElementerMedKlassenavn(htmlstring: String, klassenavn: String) =
    Jsoup.parse(htmlstring).getElementsByClass(klassenavn).size

@Language("HTML")
private val foo: String = """<!DOCTYPE html>
<html lang="no">
<head>
    <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>S&#216;knad om dagpenger</title>
    <meta content="S&#216;knad om dagpenger" name="description">
    <meta content="Dagpenger" name="subject">
    <meta content="NAV" name="author">
    <link href="https://fonts.googleapis.com" rel="preconnect">
    <link href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css2?family=Source+Sans+Pro:ital,wght@0,400;0,600;1,300&amp;display=swap"
          rel="stylesheet">
    <bookmarks>

        <bookmark href="#hovedoverskrift" name="S&#216;knad om dagpenger"></bookmark>

        <bookmark href="#infoblokk" name="Info om s&#216;knad"></bookmark>

        <bookmark href="#seksjon-reel-arbeids&#216;ker" name="Reel arbeids&#216;ker"></bookmark>
        <bookmark href="#seksjon-reel-arbeids&#216;ker-med-oppf&#216;lgingsp&#216;rsm&#197;l"
                  name="Reel arbeids&#216;ker med oppf&#216;lgingsp&#216;rsm&#197;l"></bookmark>
        <bookmark href="#seksjon-seksjon-2" name="Seksjon 2"></bookmark>
        <bookmark href="#seksjon-seksjon-2-med-oppf&#216;lgingsp&#216;rms&#197;l"
                  name="Seksjon 2 med oppf&#216;lgingsp&#216;rms&#197;l"></bookmark>

    </bookmarks>
    <style>body {
        font -family: 'Source Sans Pro';
        font -style: normal;
        width: 600px;
        padding: 0 40px 40px 40px;
        InputType .
        color: rgb(38, 38, 38);
    }

    h1 {
        font -weight: 600;
        font -size: 32px;
        line -height: 40px;
    }

    h2 {
        margin -bottom: 10px;
        font -weight: 600;
        font -size: 20px;
        line -height: 28px;
    }

    .seksjon h3 {
        margin -bottom: 0;
        font -weight: 600;
        font -size: 18px;
        line -height: 24px;
    }

    p {
        font -size: 18px;
        line -height: 24px;
        margin -bottom: 2px;
        margin -top: 2px;
        font -weight: 400;
    }

    .boldSpan {
        font -weight: bold;
    }

    .hjelpetekst {
        background -InputType .
        color: #E5E5E5;
        margin -left: 10px;
        margin -top: 5px;
        padding: 2px;
    }

    .infotekst {
        margin: 5px 0 0 0;
    }

    @page {
        size: A4 portrait;
        @top -right {
        content: counter(page);
        font -family: 'Source Sans Pro';
        padding -right: 15px;
    }
    }</style>
</head>
<body>
<h1 id="hovedoverskrift">S&#216;knad om dagpenger</h1>
<div class="infoblokk" id="infoblokk">
    <p>
        <span class="boldSpan">F&#216;dselsnummer: </span>12345678910</p>
    <p>
        <span class="boldSpan">Dato sendt: </span>24.06.2022 12:43</p>
</div>
<div class="seksjon" id="seksjon-reel-arbeids&#216;ker">
    <h2>Reel arbeids&#216;ker</h2>
    <div>
        <h3>Kan du jobbe b&#197;de heltid og deltid?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
    <div>
        <h3>Kan du jobbe i hele Norge?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
    <div>
        <h3>Kan du ta alle typer arbeid?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
    <div>
        <h3>Er du villig til &#197; bytte yrke eller g&#197; ned i l&#216;nn?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
    <div>
        <h3>Funker flersvar?</h3>
        <ul>
            <li>Ett svaralternativ</li>
            <li>ja</li>
            <li>nei</li>
        </ul>
    </div>
</div>
<div class="seksjon" id="seksjon-reel-arbeids&#216;ker-med-oppf&#216;lgingsp&#216;rsm&#197;l">
    <h2>Reel arbeids&#216;ker med oppf&#216;lgingsp&#216;rsm&#197;l</h2>
    <div>
        <h3>Kan du jobbe b&#197;de heltid og deltid?</h3>
        <p>
            <span class="boldSpan">Svar: </span>nei</p>
        <div>
            <h3>Hvorfor ikke?</h3>
            <p>
                <span class="boldSpan">Svar: </span>Fordi s&#197;nn kan det v&#198;re att det er at det er noen ganger
                at s&#197;nn kan det v&#198;re</p>
        </div>
        <div>
            <h3>Et annet sp&#216;rms&#197;l</h3>
            <p>
                <span class="boldSpan">Svar: </span>med et annet svar som ogs&#197; har oppf&#216;lging</p>
            <div>
                <h3>Hvorfor s&#197; mye oppf&#216;lging?</h3>
                <p>
                    <span class="boldSpan">Svar: </span>Fordi vi f&#216;lger opp all oppf&#216;lginga selvf&#216;lgelig
                </p>
            </div>
        </div>
    </div>
    <div>
        <h3>Kan du jobbe i hele Norge?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
    <div>
        <h3>Kan du ta alle typer arbeid?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
    <div>
        <h3>Er du villig til &#197; bytte yrke eller g&#197; ned i l&#216;nn?</h3>
        <p>
            <span class="boldSpan">Svar: </span>Ja</p>
    </div>
</div>
<div class="seksjon" id="seksjon-seksjon-2">
    <h2>Seksjon 2</h2>
    <div>
        <h3>Dett er spm 1</h3>
        <p>
            <span class="boldSpan">Svar: </span>svar 1</p>
    </div>
    <div>
        <h3>Dett er spm 2</h3>
        <p>
            <span class="boldSpan">Svar: </span>svar 2</p>
    </div>
    <div>
        <h3>Dett er spm 3</h3>
        <p>
            <span class="boldSpan">Svar: </span>svar 3</p>
    </div>
</div>
<div class="seksjon" id="seksjon-seksjon-2-med-oppf&#216;lgingsp&#216;rms&#197;l">
    <h2>Seksjon 2 med oppf&#216;lgingsp&#216;rms&#197;l</h2>
    <div>
        <h3>Dett er spm 1</h3>
        <p>
            <span class="boldSpan">Svar: </span>svar 1</p>
    </div>
    <div>
        <h3>Dett er spm 2 som skal ha oppf&#216;lgingsp&#216;rsm&#197;l</h3>
        <p>
            <span class="boldSpan">Svar: </span>svar 2</p>
        <div>
            <h3>Hvorfor ikke?</h3>
            <p>
                <span class="boldSpan">Svar: </span>Fordi s&#197;nn kan det v&#198;re att det er at det er noen ganger
                at s&#197;nn kan det v&#198;re</p>
        </div>
        <div>
            <h3>Et annet sp&#216;rms&#197;l</h3>
            <p>
                <span class="boldSpan">Svar: </span>med et annet svar som ogs&#197; har oppf&#216;lging</p>
            <div>
                <h3>Hvorfor s&#197; mye oppf&#216;lging?</h3>
                <p>
                    <span class="boldSpan">Svar: </span>Fordi vi f&#216;lger opp all oppf&#216;lginga selvf&#216;lgelig
                </p>
            </div>
        </div>
    </div>
    <div>
        <h3>Dett er spm 3</h3>
        <p>
            <span class="boldSpan">Svar: </span>svar 3</p>
    </div>
</div>
</body>
</html>
"""
