package it.hendorsoftware.medishelf.domain.model

import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Test unitari del modello domain minimo del medicinale.
 */
class MedicineModelTest {

    /**
     * Verifica che il dominio consenta una voce con soli dati minimi e senza quantita.
     */
    @Test
    fun shouldAllowMedicineWithoutQuantity() {
        val medicine = baseMedicine(quantity = null)

        assertNull(medicine.quantity)
    }

    /**
     * Verifica che il dominio consenta una voce senza data di scadenza.
     */
    @Test
    fun shouldAllowMedicineWithoutExpirationDate() {
        val medicine = baseMedicine(expirationDate = null)

        assertNull(medicine.expirationDate)
    }

    /**
     * Verifica che la quantita possa essere pari a zero ma non perda unita e soglia.
     */
    @Test
    fun shouldKeepQuantityInfoWhenQuantityIsKnown() {
        val quantityInfo = QuantityInfo(
            amount = 0.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        )

        assertEquals(0.0, quantityInfo.amount, 0.0)
        assertEquals("compresse", quantityInfo.unit)
        assertEquals(2.0, requireNotNull(quantityInfo.lowStockThreshold), 0.0)
    }

    /**
     * Verifica che una quantita negativa non possa entrare nel dominio.
     */
    @Test
    fun shouldRejectNegativeQuantity() {
        assertThrows(IllegalArgumentException::class.java) {
            QuantityInfo(
                amount = -1.0,
                unit = "compresse",
                lowStockThreshold = null,
            )
        }
    }

    /**
     * Verifica che il nome resti l'unico campo obbligatorio ma non possa essere vuoto.
     */
    @Test
    fun shouldRejectBlankMedicineName() {
        assertThrows(IllegalArgumentException::class.java) {
            baseMedicine().copy(name = " ")
        }
    }

    /**
     * Verifica che gli stati minimi previsti dal dominio siano disponibili.
     */
    @Test
    fun shouldExposeMinimumMedicineStatuses() {
        val statuses = MedicineStatus.entries.toSet()

        assertEquals(
            setOf(
                MedicineStatus.VALID,
                MedicineStatus.EXPIRING,
                MedicineStatus.EXPIRED,
                MedicineStatus.OUT_OF_STOCK,
                MedicineStatus.NO_EXPIRATION_DATE,
            ),
            statuses,
        )
    }

    private fun baseMedicine(
        quantity: QuantityInfo? = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        ),
        expirationDate: LocalDate? = LocalDate.of(2026, 12, 31),
    ): Medicine {
        val now = Instant.parse("2026-05-18T10:00:00Z")

        return Medicine(
            id = MedicineId(1L),
            name = "Paracetamolo",
            packageForm = "Compresse",
            quantity = quantity,
            expirationDate = expirationDate,
            storageLocation = "Bagno",
            notes = null,
            isArchived = false,
            createdAt = now,
            updatedAt = now,
            archivedAt = null,
        )
    }
}
