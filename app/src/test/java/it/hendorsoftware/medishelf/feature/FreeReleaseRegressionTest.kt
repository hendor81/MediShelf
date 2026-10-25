package it.hendorsoftware.medishelf.feature

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.UserSettings
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.*
import it.hendorsoftware.medishelf.feature.archive.ArchiveViewModel
import it.hendorsoftware.medishelf.feature.expiry.ExpiryViewModel
import it.hendorsoftware.medishelf.feature.home.HomeViewModel
import it.hendorsoftware.medishelf.feature.inventory.InventoryStatusFilter
import it.hendorsoftware.medishelf.feature.inventory.InventoryViewModel
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailQuantityFeedback
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailViewModel
import it.hendorsoftware.medishelf.feature.medicineform.MedicineFormViewModel
import it.hendorsoftware.medishelf.feature.settings.SettingsFeedback
import it.hendorsoftware.medishelf.feature.settings.SettingsViewModel
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Regressioni dei percorsi Free attraverso ViewModel e use case reali collegati
 * agli stessi repository fake. Le aspettative usano una data fissa e valori
 * espliciti; i timestamp generati dalle operazioni non determinano gli esiti.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FreeReleaseRegressionTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    /** Il solo nome basta per comparire nelle viste e nel dettaglio senza quantità fittizie. */
    @Test
    fun shouldShowMinimalFormSaveAcrossCoreViews() = runTest {
        val app = App()
        app.form.onNameChanged("  Paracetamolo  ")
        app.form.onSaveClick()
        advanceUntilIdle()

        assertTrue(app.form.uiState.value.isSaved)
        val saved = app.medicines.currentMedicines().single()
        assertEquals("Paracetamolo", saved.name)
        assertNull(saved.quantity)
        assertNull(saved.expirationDate)
        val item = app.inventory.uiState.value.medicines.single()
        assertEquals("Paracetamolo", item.name)
        assertEquals(MedicineStatusBadgeStatus.NoExpiration, item.status)
        assertNull(item.quantity)
        assertEquals(listOf(item.id), app.expiry.uiState.value.noExpirationMedicines.map { it.id })
        assertEquals(1, app.home.uiState.value.activeMedicineCount)
        assertEquals(0, app.home.uiState.value.lowStockMedicineCount)
        assertEquals(0, app.home.uiState.value.expiredMedicineCount)
        assertEquals(0, app.home.uiState.value.expiringMedicineCount)

        app.detail.loadMedicine(item.id)
        app.detail.onQuantityDecrementClick()
        advanceUntilIdle()
        assertEquals(item.name, app.detail.uiState.value.medicine?.name)
        assertNull(app.detail.uiState.value.medicine?.quantity)
        assertEquals(MedicineDetailQuantityFeedback.MissingQuantity, app.detail.uiState.value.quantityFeedback)
        assertEquals(saved, app.medicines.currentMedicines().single())
    }

    /** Modifica, esaurimento, archivio e ripristino aggiornano le viste già aperte. */
    @Test
    fun shouldKeepViewsConsistentThroughoutMedicineLifecycle() = runTest {
        val original = medicine(42)
        val app = App(listOf(original))
        app.form.loadMedicineForEdit("42")
        advanceUntilIdle()
        app.form.onNameChanged("  Ibuprofene aggiornato  ")
        app.form.onQuantityChanged("1")
        app.form.onQuantityUnitChanged("compresse")
        app.form.onLowStockThresholdChanged("2")
        app.form.onExpirationDateChanged("2026-05-18")
        app.form.onSaveClick()
        advanceUntilIdle()

        val updated = app.medicines.currentMedicines().single()
        assertEquals(original.id, updated.id)
        assertEquals(original.createdAt, updated.createdAt)
        assertEquals("Ibuprofene aggiornato", updated.name)
        assertEquals(listOf("42"), app.expiry.uiState.value.expiringMedicines.map { it.id })
        assertEquals(1, app.home.uiState.value.expiringMedicineCount)
        assertEquals(1, app.home.uiState.value.lowStockMedicineCount)
        app.inventory.onSearchQueryChanged("AGGIORNATO")
        advanceUntilIdle()
        assertEquals("Ibuprofene aggiornato", app.inventory.uiState.value.medicines.single().name)

        app.detail.loadMedicine("42")
        app.detail.onQuantityDecrementClick()
        advanceUntilIdle()
        assertEquals(MedicineStatusBadgeStatus.OutOfStock, app.detail.uiState.value.medicine?.status)
        assertEquals(MedicineStatusBadgeStatus.OutOfStock, app.inventory.uiState.value.medicines.single().status)
        assertTrue(app.expiry.uiState.value.isEmpty)
        assertEquals(0, app.home.uiState.value.expiringMedicineCount)
        assertEquals(0, app.home.uiState.value.lowStockMedicineCount)
        app.detail.onQuantityDecrementClick()
        advanceUntilIdle()
        assertEquals(MedicineDetailQuantityFeedback.AlreadyZero, app.detail.uiState.value.quantityFeedback)
        assertEquals(0.0, app.medicines.currentMedicines().single().quantity!!.amount, 0.0)

        app.detail.onPrimaryActionClick()
        advanceUntilIdle()
        assertTrue(app.detail.uiState.value.hasArchiveCompleted)
        assertTrue(app.inventory.uiState.value.medicines.isEmpty())
        assertTrue(app.home.uiState.value.isEmpty)
        assertEquals(listOf("42"), app.archive.uiState.value.medicines.map { it.id })
        app.detail.onNavigationHandled()
        app.detail.loadMedicine("42")
        app.detail.onPrimaryActionClick()
        advanceUntilIdle()
        assertFalse(app.detail.uiState.value.medicine!!.isArchived)
        assertNull(app.medicines.currentMedicines().single().archivedAt)
        assertTrue(app.archive.uiState.value.medicines.isEmpty())
        assertEquals(1, app.home.uiState.value.activeMedicineCount)
        assertEquals(listOf("42"), app.inventory.uiState.value.medicines.map { it.id })
    }

    /** Le preferenze reali pilotano conteggi e conferme senza eliminare voci per annullamento. */
    @Test
    fun shouldApplySettingsAndKeepDestructiveActionsExplicit() = runTest {
        val app = App(listOf(medicine(1), medicine(2).copy(isArchived = true, archivedAt = instant)))
        advanceUntilIdle()
        assertEquals(0, app.home.uiState.value.expiringMedicineCount)
        app.settings.onThresholdClick()
        app.settings.onThresholdInputChanged("31")
        app.settings.onThresholdSaveClick()
        app.inventory.onStatusFilterSelected(InventoryStatusFilter.ExpiringSoon)
        advanceUntilIdle()
        assertEquals(31, app.settings.uiState.value.expiringThresholdDays)
        assertEquals(1, app.home.uiState.value.expiringMedicineCount)
        assertEquals(listOf("1"), app.inventory.uiState.value.medicines.map { it.id })
        assertEquals(listOf("1"), app.expiry.uiState.value.expiringMedicines.map { it.id })

        app.settings.onRemovalPreferenceSelected(false)
        app.detail.loadMedicine("1")
        app.detail.onPrimaryActionClick()
        advanceUntilIdle()
        assertTrue(app.detail.uiState.value.isDeleteDialogVisible)
        assertNotNull(app.medicines.getMedicineById(MedicineId(1)))
        app.detail.onDeleteDismissed()
        assertFalse(app.detail.uiState.value.isDeleteDialogVisible)
        assertEquals(2, app.medicines.currentMedicines().size)

        app.settings.onClearArchiveClick()
        app.settings.onClearArchiveDismissed()
        assertEquals(1, app.archive.uiState.value.medicines.size)
        app.settings.onClearArchiveClick()
        app.settings.onClearArchiveConfirmed()
        advanceUntilIdle()
        assertEquals(listOf(MedicineId(1)), app.medicines.currentMedicines().map { it.id })
        assertTrue(app.archive.uiState.value.medicines.isEmpty())
        assertEquals(SettingsFeedback.ARCHIVE_CLEARED, app.settings.uiState.value.feedback)
        assertEquals(1, app.home.uiState.value.activeMedicineCount)

        app.detail.onPrimaryActionClick()
        app.detail.onDeleteConfirmed()
        advanceUntilIdle()
        assertTrue(app.detail.uiState.value.hasDeleteCompleted)
        assertNull(app.medicines.getMedicineById(MedicineId(1)))
        assertTrue(app.medicines.currentMedicines().isEmpty())
        assertTrue(app.home.uiState.value.isEmpty)
        assertTrue(app.expiry.uiState.value.isEmpty)
    }

    /** Fixture condivisa: le operazioni passano dai ViewModel, senza aggiornamenti diretti ai fake. */
    private class App(initial: List<Medicine> = emptyList()) {
        val medicines = FakeMedicineRepository(initial)
        private val preferences = FakeUserSettingsRepository(UserSettings(expiringThresholdDays = 30))
        private val calculator = MedicineStatusCalculator(FakeDateProvider(today))
        private val observeSettings = ObserveUserSettingsUseCase(preferences)
        private val active = GetActiveMedicinesUseCase(medicines)
        private val getMedicine = GetMedicineByIdUseCase(medicines)
        val form = MedicineFormViewModel(AddMedicineUseCase(medicines), getMedicine, UpdateMedicineUseCase(medicines))
        val inventory = InventoryViewModel(active, observeSettings, calculator)
        val home = HomeViewModel(GetHomeSummaryUseCase(medicines, preferences, calculator))
        val expiry = ExpiryViewModel(active, observeSettings, calculator)
        val archive = ArchiveViewModel(GetArchivedMedicinesUseCase(medicines))
        val detail = MedicineDetailViewModel(
            getMedicine, ArchiveMedicineUseCase(medicines), DeleteMedicineUseCase(medicines),
            UpdateMedicineQuantityUseCase(medicines), observeSettings,
            RestoreArchivedMedicineUseCase(medicines), calculator,
        )
        val settings = SettingsViewModel(
            observeSettings, UpdateThemeModeUseCase(preferences), UpdateExpiringThresholdDaysUseCase(preferences),
            UpdateNotificationsEnabledUseCase(preferences), UpdatePreferArchiveOverDeleteUseCase(preferences),
            ClearArchiveUseCase(medicines),
        )
    }

    private companion object {
        val today: LocalDate = LocalDate.of(2026, 5, 18)
        val instant: Instant = Instant.parse("2026-05-01T10:00:00Z")

        /** Voce iniziale oltre la soglia, con quantità assente e identità persistente. */
        fun medicine(id: Long) = Medicine(
            id = MedicineId(id), name = "Ibuprofene", packageForm = "Compresse", quantity = null,
            expirationDate = today.plusDays(31), storageLocation = "Bagno", notes = "Confezione integra",
            isArchived = false, createdAt = instant, updatedAt = instant, archivedAt = null,
        )
    }
}
