package no.nav.dagpenger.soknad

import no.nav.dagpenger.soknad.ArkiverbartDokument.DokumentVariant.BRUTTO
import no.nav.dagpenger.soknad.ArkiverbartDokument.DokumentVariant.NETTO
import no.nav.dagpenger.soknad.pdf.URNResponse
import org.junit.jupiter.api.Assertions.assertEquals
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

    private fun bruttoTestDokuemnt() = ArkiverbartDokument.brutto("<!DOCTYPE html>")
    private fun nettoTestDokuemnt() = ArkiverbartDokument.netto("<!DOCTYPE html>")
}