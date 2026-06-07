package it.hendorsoftware.medishelf.feature.expiry

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.GetActiveMedicinesUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
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
 * Test unitari per il ViewModel dello Scadenzario.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExpiryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica che il ViewModel separi in scadenza, scaduti e senza data usando lo stato domain.
     */
    @Test
    fun shouldSplitMedicinesIntoExpirySections() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        id = MedicineId(1L),
                        name = "In scadenza",
                        expirationDate = LocalDate.of(2026, 5, 31),
                    ),
                    sampleMedicine(
                        id = MedicineId(2L),
                        name = "Scaduto",
                        expirationDate = LocalDate.of(2026, 5, 1),
                    ),
                    sampleMedicine(
                        id = MedicineId(3L),
                        name = "Senza scadenza",
                        expirationDate = null,
                    ),
                    sampleMedicine(
                        id = MedicineId(4L),
                        name = "Valido",
                        expirationDate = LocalDate.of(2026, 7, 10),
                    ),
                    sampleMedicine(
                        id = MedicineId(5L),
                        name = "Esaurito",
                        quantity = QuantityInfo(
                            amount = 0.0,
                            unit = "compresse",
                            lowStockThreshold = 2.0,
                        ),
                        expirationDate = LocalDate.of(2026, 5, 1),
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertFalse(uiState.isLoading)
        assertEquals(listOf("In scadenza"), uiState.expiringMedicines.map { item -> item.name })
        assertEquals(listOf("Scaduto"), uiState.expiredMedicines.map { item -> item.name })
        assertEquals(
            listOf("Senza scadenza"),
            uiState.noExpirationMedicines.map { item -> item.name },
        )
    }

    /**
     * Verifica che le sezioni con scadenza siano ordinate cronologicamente.
     */
    @Test
    fun shouldSortDatedSectionsChronologically() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        id = MedicineId(1L),
                        name = "Scade dopo",
                        expirationDate = LocalDate.of(2026, 6, 10),
                    ),
                    sampleMedicine(
                        id = MedicineId(2L),
                        name = "Scade prima",
                        expirationDate = LocalDate.of(2026, 5, 20),
                    ),
                    sampleMedicine(
                        id = MedicineId(3L),
                        name = "Scaduto recente",
                        expirationDate = LocalDate.of(2026, 5, 10),
                    ),
                    sampleMedicine(
                        id = MedicineId(4L),
                        name = "Scaduto vecchio",
                        expirationDate = LocalDate.of(2026, 4, 1),
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertEquals(
            listOf("Scade prima", "Scade dopo"),
            uiState.expiringMedicines.map { item -> item.name },
        )
        assertEquals(
            listOf("Scaduto vecchio", "Scaduto recente"),
            uiState.expiredMedicines.map { item -> item.name },
        )
    }

    /**
     * Verifica che lo stato vuoto sia esposto quando non ci sono voci rilevanti.
     */
    @Test
    fun shouldExposeEmptyStateWhenNoRelevantMedicinesExist() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        id = MedicineId(1L),
                        name = "Valido",
                        expirationDate = LocalDate.of(2026, 7, 10),
                    ),
                    sampleMedicine(
                        id = MedicineId(2L),
                        name = "Archiviato",
                        expirationDate = LocalDate.of(2026, 5, 20),
                        isArchived = true,
                        archivedAt = Instant.parse("2026-05-10T10:00:00Z"),
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertFalse(uiState.isLoading)
        assertTrue(uiState.isEmpty)
        assertTrue(uiState.expiringMedicines.isEmpty())
        assertTrue(uiState.expiredMedicines.isEmpty())
        assertTrue(uiState.noExpirationMedicines.isEmpty())
    }

    /**
     * Verifica il mapping dei dati opzionali verso il modello UI della sezione senza data.
     */
    @Test
    fun shouldMapNoExpirationMedicineFields() = runTest {
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

        val item = viewModel.uiState.value.noExpirationMedicines.single()

        assertEquals("1", item.id)
        assertEquals("Paracetamolo", item.name)
        assertEquals(MedicineStatusBadgeStatus.NoExpiration, item.status)
        assertNull(item.packageForm)
        assertNull(item.expirationDate)
        assertNull(item.quantity)
        assertNull(item.storageLocation)
    }

    private fun createViewModel(repository: FakeMedicineRepository): ExpiryViewModel =
        ExpiryViewModel(
            getActiveMedicinesUseCase = GetActiveMedicinesUseCase(repository),
            observeUserSettingsUseCase = ObserveUserSettingsUseCase(FakeUserSettingsRepository()),
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
