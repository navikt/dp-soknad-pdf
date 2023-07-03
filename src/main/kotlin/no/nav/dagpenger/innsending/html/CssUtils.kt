package no.nav.dagpenger.innsending.html

import kotlinx.html.HEAD
import kotlinx.html.style
import kotlinx.html.unsafe
import no.nav.dagpenger.innsending.pdf.fileAsString

internal fun HEAD.søknadPdfStyle() {
    style {
        unsafe {
            raw(
                "/pdf.css".fileAsString(),
            )
        }
    }
}
