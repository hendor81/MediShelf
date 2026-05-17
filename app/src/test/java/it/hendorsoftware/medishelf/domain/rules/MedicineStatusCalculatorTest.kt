package it.hendorsoftware.medishelf.domain.rules

import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test unitari per la classificazione dello stato del medicinale.
 */
class MedicineStatusCalculatorTest {

    private val today = LocalDate.of(2026, 5, 18)
    private val calculator = MedicineStatusCalculator(FakeDateProvider(today))

    /**
     * Verifica che un medicinale con scadenza oltre soglia sia valido.
     */
    @Test
    fun shouldReturnValidWhenExpirationDateIsAfterThreshold() {
        val status = calculator.calculate(
            medicine = baseMedicine(expirationDate = today.plusDays(31)),
            expiringThresholdDays = 30,
        )

        assertEquals(MedicineStatus.VALID, status)
    }

    /**
     * Verifica che una scadenza futura entro soglia sia classificata in scadenza.
     */
    @Test
    fun shouldReturnExpiringWhenExpirationDateIsWithinThreshold() {
        val status = calculator.calculate(
            medicine = baseMedicine(expirationDate = today.plusDays(30)),
            expiringThresholdDays = 30,
        )

        assertEquals(MedicineStatus.EXPIRING, status)
    }

    /**
     * Verifica che una scadenza precedente alla data corrente sia scaduta.
     */
    @Test
    fun shouldReturnExpiredWhenExpirationDateIsBeforeToday() {
        val status = calculator.calculate(
            medicine = baseMedicine(expirationDate = today.minusDays(1)),
            expiringThresholdDays = 30,
        )

        assertEquals(MedicineStatus.EXPIRED, status)
    }

    /**
     * Verifica che una quantita nota pari a zero abbia priorita sulla scadenza.
     */
    @Test
    fun shouldReturnOutOfStockWhenQuantityIsZero() {
        val status = calculator.calculate(
            medicine = baseMedicine(
                quantity = QuantityInfo(
                    amount = 0.0,
                    unit = "compresse",
                    lowStockThreshold = 2.0,
                ),
                expirationDate = today.plusDays(90),
            ),
            expiringThresholdDays = 30,
        )

        assertEquals(MedicineStatus.OUT_OF_STOCK, status)
    }

    /**
     * Verifica che una scadenza assente venga esposta come informazione mancante.
     */
    @Test
    fun shouldReturnNoExpirationDateWhenExpirationDateIsMissing() {
        val status = calculator.calculate(
            medicine = baseMedicine(expirationDate = null),
            expiringThresholdDays = 30,
        )

        assertEquals(MedicineStatus.NO_EXPIRATION_DATE, status)
    }

    /**
     * Verifica che la quantita assente non produca lo stato esaurito.
     */
    @Test
    fun shouldNotReturnOutOfStockWhenQuantityIsMissing() {
        val status = calculator.calculate(
            medicine = baseMedicine(
                quantity = null,
                expirationDate = today.plusDays(31),
            ),
            expiringThresholdDays = 30,
        )

        assertEquals(MedicineStatus.VALID, status)
    }

    /**
     * Verifica che con soglia zero solo la scadenza odierna sia in scadenza.
     */
    @Test
    fun shouldReturnValidWithZeroThresholdWhenExpirationDateIsAfterToday() {
        val status = calculator.calculate(
            medicine = baseMedicine(expirationDate = today.plusDays(1)),
            expiringThresholdDays = 0,
        )

        assertEquals(MedicineStatus.VALID, status)
    }

    /**
     * Verifica che la scadenza coincidente con oggi sia inclusa nella soglia.
     */
    @Test
    fun shouldReturnExpiringWhenExpirationDateIsToday() {
        val status = calculator.calculate(
            medicine = baseMedicine(expirationDate = today),
            expiringThresholdDays = 0,
        )

        assertEquals(MedicineStatus.EXPIRING, status)
    }

    private fun baseMedicine(
        quantity: QuantityInfo? = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        ),
        expirationDate: LocalDate? = today.plusDays(90),
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
