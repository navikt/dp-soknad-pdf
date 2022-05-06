package no.nav.dagpenger.soknad.html

import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.STYLE
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.unsafe

internal fun String.xhtmlCompliant() = this
    .replace("&oslash;", "ø")
    .replace("&aring;", "å")
    .replace(
        Regex("(?<=<(meta|META)[a-zA-ZæøåÆØÅ=\\\"\\/\\s\\-\\.\\;0-9]{1,1000})>"),
        replacement = "/>"
    )
    .replace(
        Regex(pattern = "(?<=<(link[\\sa-zA-ZæøåÆØÅ=\\\"\\:\\/\\.0-9\\?\\+\\,@\\;\\&]{1,1000}))>"),
        replacement = "/>"
    )

internal fun HEAD.pdfa(pdfAKrav: HtmlModell.PdfAKrav) {
    meta {
        name = "description"
        content = pdfAKrav.description
    }
}

internal fun HEAD.fontimports() {
    link {
        rel = "preconnect"
        href = "https://fonts.googleapis.com"
    }
    link {
        rel = "preconnect"
        href = "https://fonts.gstatic.com"
    }
    link {
        href = "https://fonts.googleapis.com/css2?family=Source+Sans+Pro:ital,wght@0,400;0,600;1,300&display=swap"
        rel = "stylesheet"
    }
}

internal fun STYLE.søknadPdfStyle() {
    unsafe {
        //language=CSS
        raw(
            """
                     body {
                         font-family: 'Source Sans Pro';
                     }
                     
                     div{
                         width: 500px;
                     }
                     p {
                         margin-bottom: 2px;
                         margin-top: 2px;
                     }
                     
                     h2 {
                         margin-bottom: 10px;
                     }
                     
                     .seksjon h3 {
                         margin-bottom: 0;
                     }
                     
                     .boldSpan {
                         font-weight: bold;
                     }
                     
                     .hjelpetekst {
                         background-color: rgb(171, 178, 185);
                         margin-left: 10px;
                         margin-top: 5px;
                         padding: 2px;
                     }
                     
                     .infotekst {
                         margin: 5px 0 0 0;
                     }
                     
                            """.trimIndent()
        )
    }
}

internal fun DIV.boldSpanP(boldTekst: String, vanligTekst: String, divider: String = ":") {
    p {
        span(classes = "boldSpan") { +"$boldTekst: " }
        +vanligTekst
    }
}

internal fun DIV.spmDiv(spmSvar: HtmlModell.SporsmalSvar, språk: HtmlModell.SøknadSpråk) {
    div {
        h3 { +spmSvar.sporsmal }
        if (spmSvar.infotekst != null) {
            p(classes = "infotekst") { +spmSvar.infotekst }
        }
        if (spmSvar.hjelpeTekst != null) {
            p(classes = "hjelpetekst") {
                +spmSvar.hjelpeTekst
            }
        }
        boldSpanP(språk.svar, spmSvar.svar)
    }
}