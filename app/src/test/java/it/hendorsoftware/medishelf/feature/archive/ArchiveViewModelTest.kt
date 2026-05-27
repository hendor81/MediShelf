package it.hendorsoftware.medishelf.feature.archive

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.usecase.GetArchivedMedicinesUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test unitari per il ViewModel dell'Archivio.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ArchiveViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica che l'archivio esponga solo medicinali archiviati gia mappati per la UI.
     */
    @Test
    fun shouldLoadOnlyArchivedMedicines() = runTest {
        val activeMedicine = sampleMedicine(isArchived = false, archivedAt = null)
        val archivedMedicine = sampleMedicine(
            id = MedicineId(2L),
            name = "Sciroppo tosse",
            packageForm = "Flacone",
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
        assertEquals("2", item.id)
        assertEquals("Sciroppo tosse", item.name)
        assertEquals("Flacone", item.packageForm)
        assertEquals("31/05/2026", item.expirationDate)
        assertEquals("12 compresse", item.quantity)
        assertEquals("Bagno", item.storageLocation)
    }

    /**
     * Verifica che l'assenza di medicinali archiviati produca lo stato vuoto della schermata.
     */
    @Test
    fun shouldExposeEmptyStateWhenArchiveIsEmpty() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(sampleMedicine(isArchived = false, archivedAt = null)),
            ),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertFalse(uiState.isLoading)
        assertTrue(uiState.medicines.isEmpty())
    }

    /**
     * Verifica che i campi opzionali assenti restino nascosti anche nell'area Archivio.
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

        assertNull(item.packageForm)
        assertNull(item.quantity)
        assertNull(item.expirationDate)
        assertNull(item.storageLocation)
    }

    private fun createViewModel(repository: FakeMedicineRepository): ArchiveViewModel =
        ArchiveViewModel(
            getArchivedMedicinesUseCase = GetArchivedMedicinesUseCase(repository),
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
        isArchived: Boolean = true,
        archivedAt: Instant? = Instant.parse("2026-05-10T10:00:00Z"),
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
