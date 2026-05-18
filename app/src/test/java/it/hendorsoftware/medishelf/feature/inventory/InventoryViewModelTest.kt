package it.hendorsoftware.medishelf.feature.inventory

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.GetActiveMedicinesUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

/**
 * Test unitari per il ViewModel dell'Inventario.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica che il caricamento iniziale esponga solo medicinali attivi gia mappati per la UI.
     */
    @Test
    fun shouldLoadActiveMedicines() = runTest {
        val activeMedicine = sampleMedicine()
        val archivedMedicine = sampleMedicine(
            id = MedicineId(2L),
            name = "Archiviato",
            isArchived = true,
            archivedAt = Instant.parse("2026-05-10T10:00:00Z"),
        )
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(activeMedicine, archivedMedicine),
            ),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val item = uiState.medicines.single()

        assertFalse(uiState.isLoading)
        assertEquals("1", item.id)
        assertEquals("Paracetamolo", item.name)
        assertEquals("Compresse", item.packageForm)
        assertEquals(MedicineStatusBadgeStatus.ExpiringSoon, item.status)
        assertEquals("31/05/2026", item.expirationDate)
        assertEquals("12 compresse", item.quantity)
        assertEquals("Bagno", item.storageLocation)
    }

    /**
     * Verifica che i campi opzionali assenti restino nascosti a livello UI.
     */
    @Test
    fun shouldMapMissingOptionalFieldsAsNull() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        packageForm = null,
                        quantity = null,
                        expirationDate = null,
                        storageLocation = null,
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val item = viewModel.uiState.value.medicines.single()

        assertEquals(MedicineStatusBadgeStatus.NoExpiration, item.status)
        assertNull(item.packageForm)
        assertNull(item.quantity)
        assertNull(item.expirationDate)
        assertNull(item.storageLocation)
    }

    private fun createViewModel(repository: FakeMedicineRepository): InventoryViewModel =
        InventoryViewModel(
            getActiveMedicinesUseCase = GetActiveMedicinesUseCase(repository),
            statusCalculator = MedicineStatusCalculator(
                dateProvider = FakeDateProvider(LocalDate.of(2026, 5, 18)),
            ),
        )

    private fun sampleMedicine(
        id: MedicineId = MedicineId(1L),
        name: String = "Paracetamolo",
        packageForm: String? = "Compresse",
        quantity: QuantityInfo? = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        ),
        expirationDate: LocalDate? = LocalDate.of(2026, 5, 31),
        storageLocation: String? = "Bagno",
        isArchived: Boolean = false,
        archivedAt: Instant? = null,
    ): Medicine = Medicine(
        id = id,
        name = name,
        packageForm = packageForm,
        quantity = quantity,
        expirationDate = expirationDate,
        storageLocation = storageLocation,
        notes = null,
        isArchived = isArchived,
        createdAt = Instant.parse("2026-05-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-05-10T10:00:00Z"),
        archivedAt = archivedAt,
    )
}
