package it.hendorsoftware.medishelf.feature

import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.rules.MedicineStockRules
import it.hendorsoftware.medishelf.domain.usecase.AddMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.ArchiveMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.DeleteMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetActiveMedicinesUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetMedicineByIdUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.domain.usecase.RestoreArchivedMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateMedicineQuantityUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateMedicineUseCase
import it.hendorsoftware.medishelf.feature.inventory.InventoryStatusFilter
import it.hendorsoftware.medishelf.feature.inventory.InventoryViewModel
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailViewModel
import it.hendorsoftware.medishelf.feature.medicineform.MedicineFormFieldError
import it.hendorsoftware.medishelf.feature.medicineform.MedicineFormViewModel
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Regressioni sui flussi approvati dopo la verifica di fruibilità della issue #92. */
@OptIn(ExperimentalCoroutinesApi::class)
class UiRefinementsTest {
    @get:Rule val dispatcher = MainDispatcherRule()
    private val today = LocalDate.of(2026, 10, 23)
    private val calculator = MedicineStatusCalculator(FakeDateProvider(today))

    /** Quantità zero, assente, soglia assente e archivio non sono scorta bassa. */
    @Test fun shouldDistinguishLowStockFromOutOfStock() {
        assertTrue(MedicineStockRules.isLowStock(medicine(1, 2.0)))
        assertFalse(MedicineStockRules.isLowStock(medicine(1, 0.0)))
        assertFalse(MedicineStockRules.isLowStock(medicine(1, null)))
        assertFalse(MedicineStockRules.isLowStock(medicine(1, 3.0)))
        assertFalse(MedicineStockRules.isLowStock(medicine(1, 1.0).copy(isArchived = true)))
        assertFalse(MedicineStockRules.isLowStock(medicine(1, 1.0).copy(quantity = QuantityInfo(1.0, null, null))))
    }

    /** Il filtro Scorta bassa include anche uno scaduto sotto soglia, ma non gli esauriti. */
    @Test fun shouldFilterStockIndependentlyOfExpiration() = runTest {
        val repository = FakeMedicineRepository(listOf(
            medicine(1, 2.0), medicine(2, 0.0), medicine(3, null),
            medicine(4, 1.0).copy(expirationDate = today.minusDays(1)), medicine(5, 8.0),
        ))
        val vm = InventoryViewModel(GetActiveMedicinesUseCase(repository), settings(), calculator)
        vm.onStatusFilterSelected(InventoryStatusFilter.LowStock)
        advanceUntilIdle()
        assertEquals(listOf("1", "4"), vm.uiState.value.medicines.map { it.id })
        assertTrue(vm.uiState.value.medicines.all { it.isLowStock })
        vm.onStatusFilterSelected(InventoryStatusFilter.Attention)
        advanceUntilIdle()
        assertEquals(listOf("1", "2", "4"), vm.uiState.value.medicines.map { it.id })
    }

    /** Il filtro di attenzione comprende anche tutte le voci senza data e in scadenza. */
    @Test fun shouldIncludeAllAttentionCategories() = runTest {
        val repository = FakeMedicineRepository(listOf(
            medicine(1, null).copy(expirationDate = null),
            medicine(2, null).copy(expirationDate = today.plusDays(2)),
            medicine(3, null).copy(expirationDate = today.minusDays(1)),
            medicine(4, 8.0),
        ))
        val vm = InventoryViewModel(GetActiveMedicinesUseCase(repository), settings(), calculator)
        vm.onStatusFilterSelected(InventoryStatusFilter.Attention)
        advanceUntilIdle()
        assertEquals(listOf("1", "2", "3"), vm.uiState.value.medicines.map { it.id })
    }

    /** Il ritorno dal dettaglio non deve sovrascrivere il filtro scelto nella lista. */
    @Test fun shouldPreserveSelectedFilterAfterReturningToInventory() = runTest {
        val repo = FakeMedicineRepository(listOf(medicine(1, 2.0), medicine(2, 5.0)))
        val vm = InventoryViewModel(GetActiveMedicinesUseCase(repo), settings(), calculator)
        vm.applyInitialFilter(InventoryStatusFilter.Attention)
        vm.onStatusFilterSelected(InventoryStatusFilter.All)
        vm.applyInitialFilter(InventoryStatusFilter.Attention)
        advanceUntilIdle()
        assertEquals(InventoryStatusFilter.All, vm.uiState.value.selectedStatusFilter)
        assertEquals(2, vm.uiState.value.medicines.size)
    }

    /** Il ripristino conserva i dati e fa riapparire la voce nell'inventario attivo. */
    @Test fun shouldRestoreArchivedMedicineWithoutLosingData() = runTest {
        val original = medicine(1, 2.0).copy(isArchived = true, archivedAt = Instant.EPOCH)
        val repo = FakeMedicineRepository(listOf(original))
        val vm = detail(repo)
        vm.loadMedicine("1")
        advanceUntilIdle()
        vm.onPrimaryActionClick()
        advanceUntilIdle()
        val restored = repo.currentMedicines().single()
        assertFalse(restored.isArchived)
        assertNull(restored.archivedAt)
        assertEquals(original.quantity, restored.quantity)
        assertEquals(original.expirationDate, restored.expirationDate)
        assertFalse(requireNotNull(vm.uiState.value.medicine).isArchived)
    }

    /** La preferenza Elimina apre la conferma; non cancella al primo tap. */
    @Test fun shouldHonorDeletePreferenceWithConfirmation() = runTest {
        val preferences = FakeUserSettingsRepository()
        preferences.updatePreferArchiveOverDelete(false)
        val repo = FakeMedicineRepository(listOf(medicine(1, 5.0)))
        val vm = detail(repo, preferences)
        vm.loadMedicine("1")
        advanceUntilIdle()
        vm.onPrimaryActionClick()
        assertTrue(vm.uiState.value.isDeleteDialogVisible)
        assertTrue(repo.deletedMedicineIds.isEmpty())
        vm.onDeleteDismissed()
        vm.onSecondaryActionClick()
        advanceUntilIdle()
        assertTrue(repo.currentMedicines().single().isArchived)
    }

    /** L'evento di archiviazione viene consumato prima del ritorno alla lista. */
    @Test fun shouldConsumeArchiveNavigationEvent() = runTest {
        val vm = detail(FakeMedicineRepository(listOf(medicine(1, 5.0))))
        vm.loadMedicine("1")
        advanceUntilIdle()
        vm.onPrimaryActionClick()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.hasArchiveCompleted)
        vm.onNavigationHandled()
        assertFalse(vm.uiState.value.hasArchiveCompleted)
    }

    /** L'errore di salvataggio conserva i campi e permette un secondo tentativo. */
    @Test fun shouldRetrySaveWithoutLosingInput() = runTest {
        val storage = FakeMedicineRepository()
        var fail = true
        val repo = object : MedicineRepository by storage {
            override suspend fun saveMedicine(medicine: Medicine) {
                if (fail) throw IOException("storage unavailable")
                storage.saveMedicine(medicine)
            }
        }
        val vm = form(repo)
        vm.onNameChanged("Pomata")
        vm.onSaveClick()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.hasSaveError)
        assertFalse(vm.uiState.value.isSaving)
        assertEquals("Pomata", vm.uiState.value.name)
        fail = false
        vm.onSaveClick()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.isSaved)
        assertFalse(vm.uiState.value.hasSaveError)
        assertEquals(1, storage.savedMedicines.size)
    }

    /** Unità o soglia senza quantità non vengono più scartate silenziosamente. */
    @Test fun shouldExplainQuantityDependency() = runTest {
        val storage = FakeMedicineRepository()
        val vm = form(storage)
        vm.onNameChanged("Pomata")
        vm.onQuantityUnitChanged("g")
        vm.onLowStockThresholdChanged("2")
        vm.onSaveClick()
        assertEquals(MedicineFormFieldError.QUANTITY_REQUIRED, vm.uiState.value.quantityError)
        assertTrue(storage.savedMedicines.isEmpty())
        assertEquals("g", vm.uiState.value.quantityUnit)
        vm.onQuantityChanged("10")
        vm.onSaveClick()
        advanceUntilIdle()
        assertEquals(2.0, storage.savedMedicines.single().quantity?.lowStockThreshold)
    }

    /** Il fallimento dell'archiviazione libera le azioni e permette di riprovare. */
    @Test fun shouldRecoverFromArchiveFailure() = runTest {
        val storage = FakeMedicineRepository(listOf(medicine(1, 5.0)))
        var fail = true
        val repo = object : MedicineRepository by storage {
            override suspend fun archiveMedicine(id: MedicineId) {
                if (fail) throw IOException("storage unavailable")
                storage.archiveMedicine(id)
            }
        }
        val vm = detail(repo)
        vm.loadMedicine("1")
        advanceUntilIdle()
        vm.onPrimaryActionClick()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.hasActionError)
        assertFalse(vm.uiState.value.isActionInProgress)
        fail = false
        vm.onPrimaryActionClick()
        advanceUntilIdle()
        assertTrue(vm.uiState.value.hasArchiveCompleted)
        assertFalse(vm.uiState.value.hasActionError)
    }

    private fun settings() = ObserveUserSettingsUseCase(FakeUserSettingsRepository())
    private fun form(repo: MedicineRepository) = MedicineFormViewModel(
        AddMedicineUseCase(repo), GetMedicineByIdUseCase(repo), UpdateMedicineUseCase(repo),
    )
    private fun detail(repo: MedicineRepository, preferences: FakeUserSettingsRepository = FakeUserSettingsRepository()) =
        MedicineDetailViewModel(GetMedicineByIdUseCase(repo), ArchiveMedicineUseCase(repo),
            DeleteMedicineUseCase(repo), UpdateMedicineQuantityUseCase(repo),
            ObserveUserSettingsUseCase(preferences), RestoreArchivedMedicineUseCase(repo), calculator)

    private fun medicine(id: Long, amount: Double?) = Medicine(
        id = MedicineId(id), name = "Medicinale $id", packageForm = "compresse",
        quantity = amount?.let { QuantityInfo(it, "compresse", 2.0) },
        expirationDate = today.plusDays(60), storageLocation = null, notes = null,
        isArchived = false, createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH, archivedAt = null,
    )
}
