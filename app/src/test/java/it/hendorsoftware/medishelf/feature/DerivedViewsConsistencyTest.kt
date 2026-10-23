package it.hendorsoftware.medishelf.feature

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.GetActiveMedicinesUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetHomeSummaryUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.feature.expiry.ExpiryViewModel
import it.hendorsoftware.medishelf.feature.home.HomeViewModel
import it.hendorsoftware.medishelf.feature.inventory.InventoryStatusFilter
import it.hendorsoftware.medishelf.feature.inventory.InventoryViewModel
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Verifica lo stesso inventario attraverso i tre ViewModel e i use case reali.
 *
 * Data, repository e impostazioni sono condivisi e controllati: gli identificativi
 * attesi sono espliciti, senza ricalcolare l'oracolo con il classificatore testato.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DerivedViewsConsistencyTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /** Verifica conteggi, sezioni e filtri con tutti gli stati e una voce archiviata. */
    @Test
    fun shouldKeepDashboardCountsAndExpirySectionsConsistentWithInventory() = runTest {
        val views = Views(mixedMedicines())

        assertViews(views, defaultGroups, lowStockCount = 1)

        // Il limite della lista attenzione non deve troncare i conteggi della Home.
        assertEquals(3, views.home.uiState.value.attentionItems.size)
        assertEquals(9, views.home.uiState.value.activeMedicineCount)
    }

    /** Verifica aggiornamenti inclusivi e reversibili della soglia sui ViewModel gia attivi. */
    @Test
    fun shouldRecalculateAllViewsWhenThresholdIncreasesAndDecreases() = runTest {
        val views = Views(mixedMedicines())
        assertViews(views, defaultGroups, lowStockCount = 1)

        views.settings.updateExpiringThresholdDays(31)
        assertViews(
            views,
            defaultGroups.copy(valid = listOf("1", "10"), expiring = listOf("2", "7", "8")),
            lowStockCount = 1,
        )

        views.settings.updateExpiringThresholdDays(0)
        assertViews(
            views,
            defaultGroups.copy(valid = listOf("1", "2", "8", "10"), expiring = listOf("7")),
            lowStockCount = 1,
        )

        views.settings.updateExpiringThresholdDays(30)
        assertViews(views, defaultGroups, lowStockCount = 1)
    }

    /** Verifica che la quantita assente conservi la classificazione basata sulla scadenza. */
    @Test
    fun shouldClassifyMedicinesWithoutQuantityWithoutCountingLowStock() = runTest {
        val views = Views(
            listOf(
                medicine(1, daysUntilExpiry = 31, amount = null),
                medicine(2, daysUntilExpiry = 30, amount = null),
                medicine(3, daysUntilExpiry = -1, amount = null),
                medicine(4, daysUntilExpiry = null, amount = null),
            ),
        )

        assertViews(
            views,
            Groups(valid = listOf("1"), expiring = listOf("2"), expired = listOf("3"), noDate = listOf("4")),
            lowStockCount = 0,
        )
        views.inventory.uiState.value.medicines.forEach { assertNull(it.quantity) }
        views.home.uiState.value.attentionItems.forEach { assertNull(it.quantity) }
        val expiry = views.expiry.uiState.value
        (expiry.expiringMedicines + expiry.expiredMedicines + expiry.noExpirationMedicines)
            .forEach { assertNull(it.quantity) }
    }

    /** Verifica la priorita di esaurito anche su scaduto, in scadenza e senza data. */
    @Test
    fun shouldExcludeOutOfStockMedicinesFromAllExpirySections() = runTest {
        val views = Views(
            listOf(
                medicine(1, daysUntilExpiry = -1, amount = 0.0),
                medicine(2, daysUntilExpiry = 1, amount = 0.0),
                medicine(3, daysUntilExpiry = null, amount = 0.0),
            ),
        )

        assertViews(views, Groups(outOfStock = listOf("1", "2", "3")), lowStockCount = 0)
        assertTrue(views.expiry.uiState.value.isEmpty)
        assertFalse(views.home.uiState.value.isEmpty)
    }

    /** Verifica che quantita, archiviazione e cancellazione aggiornino tutte le viste. */
    @Test
    fun shouldKeepViewsConsistentAfterRepositoryUpdates() = runTest {
        val views = Views(mixedMedicines())
        assertViews(views, defaultGroups, lowStockCount = 1)

        views.medicines.saveMedicine(medicine(2, daysUntilExpiry = 30, amount = 0.0))
        val afterQuantityChange = defaultGroups.copy(
            expiring = listOf("7"),
            outOfStock = listOf("2", "5", "6"),
        )
        assertViews(views, afterQuantityChange, lowStockCount = 1)

        views.medicines.archiveMedicine(MedicineId(3))
        val afterArchive = afterQuantityChange.copy(expired = emptyList())
        assertViews(views, afterArchive, lowStockCount = 1)

        views.medicines.deleteMedicine(MedicineId(4))
        assertViews(views, afterArchive.copy(noDate = emptyList()), lowStockCount = 1)
    }

    /** Verifica conteggi e sezioni vuoti senza confondere il risultato con il caricamento. */
    @Test
    fun shouldExposeConsistentEmptyViewsForEmptyInventory() = runTest {
        val views = Views(emptyList())

        assertViews(views, Groups(), lowStockCount = 0)
        assertTrue(views.home.uiState.value.isEmpty)
        assertTrue(views.expiry.uiState.value.isEmpty)
        assertFalse(views.inventory.uiState.value.hasActiveMedicines)
        assertTrue(views.home.uiState.value.attentionItems.isEmpty())
    }

    /** Verifica che nessuno stato archiviato contribuisca a conteggi o liste attive. */
    @Test
    fun shouldExcludeArchivedMedicinesFromEveryDerivedView() = runTest {
        val views = Views(mixedMedicines().map { it.copy(isArchived = true, archivedAt = fixedInstant) })

        assertViews(views, Groups(), lowStockCount = 0)
        assertTrue(views.home.uiState.value.isEmpty)
        assertTrue(views.home.uiState.value.attentionItems.isEmpty())
        assertFalse(views.inventory.uiState.value.hasActiveMedicines)
        assertTrue(views.expiry.uiState.value.isEmpty)
    }

    /** Confronta identificativi e conteggi attesi, inclusi tutti i filtri Inventario. */
    private fun TestScope.assertViews(views: Views, expected: Groups, lowStockCount: Int) {
        advanceUntilIdle()
        val home = views.home.uiState.value
        val inventory = views.inventory.uiState.value
        val expiry = views.expiry.uiState.value
        assertFalse(home.isLoading)
        assertFalse(inventory.isLoading)
        assertFalse(expiry.isLoading)

        val groups = mapOf(
            MedicineStatusBadgeStatus.Valid to expected.valid,
            MedicineStatusBadgeStatus.ExpiringSoon to expected.expiring,
            MedicineStatusBadgeStatus.Expired to expected.expired,
            MedicineStatusBadgeStatus.OutOfStock to expected.outOfStock,
            MedicineStatusBadgeStatus.NoExpiration to expected.noDate,
        )
        val allIds = groups.values.flatten().sorted()
        assertEquals(allIds, inventory.medicines.map { it.id }.sorted())
        assertEquals(allIds.isNotEmpty(), inventory.hasActiveMedicines)
        groups.forEach { (status, ids) ->
            assertEquals(ids.sorted(), inventory.medicines.filter { it.status == status }.map { it.id }.sorted())
        }
        assertEquals(allIds.size, home.activeMedicineCount)
        assertEquals(expected.expiring.size, home.expiringMedicineCount)
        assertEquals(expected.expired.size, home.expiredMedicineCount)
        assertEquals(lowStockCount, home.lowStockMedicineCount)

        val sections = listOf(
            Triple(expected.expiring, expiry.expiringMedicines, MedicineStatusBadgeStatus.ExpiringSoon),
            Triple(expected.expired, expiry.expiredMedicines, MedicineStatusBadgeStatus.Expired),
            Triple(expected.noDate, expiry.noExpirationMedicines, MedicineStatusBadgeStatus.NoExpiration),
        )
        sections.forEach { (ids, items, status) ->
            assertEquals(ids.sorted(), items.map { it.id }.sorted())
            items.forEach { item ->
                assertEquals(status, item.status)
                val inventoryItem = inventory.medicines.single { it.id == item.id }
                assertEquals(inventoryItem.status, item.status)
                assertEquals(inventoryItem.expirationDate, item.expirationDate)
                assertEquals(inventoryItem.quantity, item.quantity)
            }
        }
        expiry.noExpirationMedicines.forEach { assertNull(it.expirationDate) }
        home.attentionItems.forEach { item ->
            val inventoryItem = inventory.medicines.single { it.id == item.id }
            assertEquals(item.status, inventoryItem.status)
            assertEquals(item.isLowStock, inventoryItem.isLowStock)
        }

        val filters = mapOf(
            InventoryStatusFilter.All to allIds,
            InventoryStatusFilter.Valid to expected.valid,
            InventoryStatusFilter.ExpiringSoon to expected.expiring,
            InventoryStatusFilter.Expired to expected.expired,
            InventoryStatusFilter.OutOfStock to expected.outOfStock,
            InventoryStatusFilter.NoExpirationDate to expected.noDate,
        )
        filters.forEach { (filter, ids) ->
            views.inventory.onStatusFilterSelected(filter)
            advanceUntilIdle()
            assertEquals(filter, views.inventory.uiState.value.selectedStatusFilter)
            assertEquals(ids.sorted(), views.inventory.uiState.value.medicines.map { it.id }.sorted())
        }
        views.inventory.onStatusFilterSelected(InventoryStatusFilter.All)
        advanceUntilIdle()
    }

    /** Raggruppamenti attesi, indipendenti dalla logica di produzione. */
    private data class Groups(
        val valid: List<String> = emptyList(),
        val expiring: List<String> = emptyList(),
        val expired: List<String> = emptyList(),
        val outOfStock: List<String> = emptyList(),
        val noDate: List<String> = emptyList(),
    )

    /** Collega le tre viste agli stessi fake e allo stesso provider di data fissa. */
    private class Views(initialMedicines: List<Medicine>) {
        val medicines = FakeMedicineRepository(initialMedicines)
        val settings = FakeUserSettingsRepository()
        private val calculator = MedicineStatusCalculator(FakeDateProvider(today))
        private val activeMedicines = GetActiveMedicinesUseCase(medicines)
        private val observeSettings = ObserveUserSettingsUseCase(settings)
        val home = HomeViewModel(GetHomeSummaryUseCase(medicines, settings, calculator))
        val inventory = InventoryViewModel(activeMedicines, observeSettings, calculator)
        val expiry = ExpiryViewModel(activeMedicines, observeSettings, calculator)
    }

    /** Crea un inventario misto con casi limite distinti e identificativi stabili. */
    private fun mixedMedicines(): List<Medicine> = listOf(
        medicine(1, daysUntilExpiry = 61),
        medicine(2, daysUntilExpiry = 30, amount = null),
        medicine(3, daysUntilExpiry = -1, amount = null),
        medicine(4, daysUntilExpiry = null, amount = null),
        medicine(5, daysUntilExpiry = -1, amount = 0.0),
        medicine(6, daysUntilExpiry = null, amount = 0.0),
        medicine(7, daysUntilExpiry = 0),
        medicine(8, daysUntilExpiry = 31, amount = null),
        medicine(9, daysUntilExpiry = -1).copy(isArchived = true, archivedAt = fixedInstant),
        medicine(10, daysUntilExpiry = 61, amount = 1.0),
    )

    /** Costruisce una voce con quantita e data opzionali senza dipendere dall'orologio reale. */
    private fun medicine(id: Long, daysUntilExpiry: Long?, amount: Double? = 12.0): Medicine = Medicine(
        id = MedicineId(id),
        name = "Medicinale $id",
        packageForm = null,
        quantity = amount?.let { QuantityInfo(amount = it, unit = null, lowStockThreshold = 2.0) },
        expirationDate = daysUntilExpiry?.let(today::plusDays),
        storageLocation = null,
        notes = null,
        isArchived = false,
        createdAt = fixedInstant,
        updatedAt = fixedInstant,
        archivedAt = null,
    )

    private companion object {
        private val today = LocalDate.of(2026, 5, 18)
        private val fixedInstant = Instant.parse("2026-05-18T12:00:00Z")
        private val defaultGroups = Groups(
            valid = listOf("1", "8", "10"),
            expiring = listOf("2", "7"),
            expired = listOf("3"),
            outOfStock = listOf("5", "6"),
            noDate = listOf("4"),
        )
    }
}
