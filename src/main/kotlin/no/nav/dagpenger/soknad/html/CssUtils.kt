package no.nav.dagpenger.soknad.html

import kotlinx.html.HEAD
import kotlinx.html.STYLE
import kotlinx.html.link
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
        href = "https://fonts.googleapis.com/css2?family=Source+Sans+Pro:ital,wght@0,400;0,600;1,300&display=swap"
        rel = "stylesheet"
    }
}

internal fun STYLE.søknadPdfStyle() {
    unsafe {
        raw(
            hentCss()
        )
    }
}

internal fun hentCss() = CSSUtilities::class.java.getResource("/pdf.css").readText()
