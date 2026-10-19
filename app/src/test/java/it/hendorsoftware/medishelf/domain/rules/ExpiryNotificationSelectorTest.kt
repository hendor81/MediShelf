package it.hendorsoftware.medishelf.domain.rules

import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.model.UserSettings
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Copre selezione, soglie inclusive ed esclusioni delle notifiche organizzative. */
class ExpiryNotificationSelectorTest {
    private val today = LocalDate.of(2026, 10, 20)
    private val selector = ExpiryNotificationSelector(MedicineStatusCalculator(FakeDateProvider(today)))

    /** Comprende oggi, limite incluso e scaduti; esclude le date oltre soglia. */
    @Test fun shouldSelectExpiredAndExpiringWithInclusiveThreshold() {
        val expired = medicine(today.minusDays(1))
        val dueToday = medicine(today)
        val atLimit = medicine(today.plusDays(30))
        assertEquals(listOf(expired, dueToday, atLimit), selector.select(
            listOf(expired, dueToday, atLimit, medicine(today.plusDays(31))), UserSettings(expiringThresholdDays = 30),
        ))
    }

    /** Disabilitare la preferenza esclude anche i medicinali già scaduti. */
    @Test fun shouldSelectNothingWhenDisabled() {
        assertTrue(selector.select(listOf(medicine(today.minusDays(1))),
            UserSettings(notificationsEnabled = false)).isEmpty())
    }

    /** Esclude archiviate, esaurite e prive di scadenza; ammette quantità assente. */
    @Test fun shouldExcludeArchivedOutOfStockAndMissingExpiration() {
        val eligible = medicine(today)
        assertEquals(listOf(eligible), selector.select(listOf(
            eligible.copy(isArchived = true), eligible.copy(quantity = QuantityInfo(0.0, null, null)),
            eligible.copy(expirationDate = null), eligible,
        ), UserSettings()))
    }

    /** Una nuova soglia modifica la selezione senza modificare i medicinali. */
    @Test fun shouldRespectChangedThreshold() {
        val medicine = medicine(today.plusDays(10))
        assertTrue(selector.select(listOf(medicine), UserSettings(expiringThresholdDays = 7)).isEmpty())
        assertEquals(listOf(medicine), selector.select(listOf(medicine), UserSettings(expiringThresholdDays = 10)))
    }

    private fun medicine(expiration: LocalDate?) = Medicine(
        MedicineId(1L), "Prodotto", null, null, expiration, null, null,
        false, Instant.EPOCH, Instant.EPOCH, null,
    )
}
