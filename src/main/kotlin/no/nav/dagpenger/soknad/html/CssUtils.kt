package no.nav.dagpenger.soknad.html

import kotlinx.html.HEAD
import kotlinx.html.link
import kotlinx.html.style
import kotlinx.html.unsafe
import org.apache.batik.bridge.CSSUtilities

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
        rel = "stylesheet"
        href = "https://fonts.googleapis.com/css2?family=Source+Sans+Pro:ital,wght@0,400;0,600;1,300&display=swap"
    }
}

internal fun HEAD.søknadPdfStyle() {
    style {
        unsafe {
            raw(
                hentCss("/pdf.css")
            )
        }
    }
}

internal fun hentCss(cssFilnavn: String): String =
    CSSUtilities::class.java.getResource(cssFilnavn)?.readText()
        ?: throw IllegalArgumentException("Fant ikke css fil $cssFilnavn")
