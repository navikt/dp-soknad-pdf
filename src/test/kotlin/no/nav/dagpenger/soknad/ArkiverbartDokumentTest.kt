package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.ArkiverbartDokument.DokumentVariant.BRUTTO
import no.nav.dagpenger.soknad.ArkiverbartDokument.DokumentVariant.NETTO
import no.nav.dagpenger.soknad.pdf.URNResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ArkiverbartDokumentTest {

    @Test
    fun `Lager arkiverbart dokument`() {
        ArkiverbartDokument.brutto("<!DOCTYPE html>").also {
            assertEquals(BRUTTO, it.variant)
            assertEquals("brutto.pdf", it.filnavn)
        }

        ArkiverbartDokument.netto("<!DOCTYPE html>").also {
            assertEquals(NETTO, it.variant)
            assertEquals("netto.pdf", it.filnavn)
        }
    }

    @Test
    fun `Kaster IllegalArgument Exception på feil i String-verdi`() {
        assertThrows<IllegalArgumentException> {
            ArkiverbartDokument.brutto("noe som ikke er html")
            ArkiverbartDokument.netto("noe som ikke er html")
        }
    }

    @Test
    fun `legger til URN`() {
        val urnResponse = listOf(
            URNResponse("brutto.pdf", "urn:noe:vedlegg:brutto.pdf"),
            URNResponse("netto.pdf", "urn:noe:vedlegg:netto.pdf")
        )
        listOf(nettoTestDokuemnt(), bruttoTestDokuemnt()).leggTilUrn(urnResponse).also { dokumentListe ->
            assertNotNull(dokumentListe.find { it.variant == NETTO && it.urn.endsWith(":netto.pdf") })
            assertNotNull(dokumentListe.find { it.variant == BRUTTO && it.urn.endsWith(":brutto.pdf") })
        }
    }

    private fun bruttoTestDokuemnt() = ArkiverbartDokument.brutto("<!DOCTYPE html>")
    private fun nettoTestDokuemnt() = ArkiverbartDokument.netto("<!DOCTYPE html>")
}