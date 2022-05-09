package no.nav.dagpenger.soknad.html

import java.time.LocalDate
import java.util.UUID

internal class SoknadSupplier(dpSoknadUrl: String, soknadTokenSupplier: () -> String) {

    suspend fun hentSoknad(id: UUID): HtmlModell {
        return HtmlModell(
            seksjoner = listOf(),
            metaInfo = HtmlModell.MetaInfo(hovedOverskrift = "", tittel = ""),
            pdfAKrav = HtmlModell.PdfAKrav(description = "todo", subject = "todo", author = "todo"),
            infoBlokk = HtmlModell.InfoBlokk(fødselsnummer = "todo", datoSendt = LocalDate.now().toString())
        )
    }
}
