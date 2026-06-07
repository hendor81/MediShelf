package it.hendorsoftware.medishelf.feature.home

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.GetHomeSummaryUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/**
 * Test unitari per il ViewModel della Home / Dashboard.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica i conteggi principali richiesti dalla dashboard.
     */
    @Test
    fun shouldExposeDashboardCounts() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        id = MedicineId(1L),
                        name = "Valido",
                        expirationDate = LocalDate.of(2026, 7, 20),
                    ),
                    sampleMedicine(
                        id = MedicineId(2L),
                        name = "In scadenza",
                        expirationDate = LocalDate.of(2026, 6, 20),
                    ),
                    sampleMedicine(
                        id = MedicineId(3L),
                        name = "Scaduto",
                        expirationDate = LocalDate.of(2026, 5, 1),
                    ),
                    sampleMedicine(
                        id = MedicineId(4L),
                        name = "Scorta bassa",
                        quantity = QuantityInfo(
                            amount = 1.0,
                            unit = "confezione",
                            lowStockThreshold = 2.0,
                        ),
                        expirationDate = LocalDate.of(2026, 7, 20),
                    ),
                    sampleMedicine(
                        id = MedicineId(5L),
                        name = "Archiviato",
                        isArchived = true,
                        archivedAt = Instant.parse("2026-05-10T10:00:00Z"),
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertFalse(uiState.isLoading)
        assertEquals(4, uiState.activeMedicineCount)
        assertEquals(1, uiState.expiringMedicineCount)
        assertEquals(1, uiState.expiredMedicineCount)
        assertEquals(1, uiState.lowStockMedicineCount)
    }

    /**
     * Verifica che la sezione attenzione sia limitata e ordinata per priorita.
     */
    @Test
    fun shouldExposePrioritizedAttentionItems() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        id = MedicineId(1L),
                        name = "In scadenza",
                        expirationDate = LocalDate.of(2026, 6, 20),
                    ),
                    sampleMedicine(
                        id = MedicineId(2L),
                        name = "Scaduto",
                        expirationDate = LocalDate.of(2026, 5, 1),
                    ),
                    sampleMedicine(
                        id = MedicineId(3L),
                        name = "Scorta bassa",
                        quantity = QuantityInfo(
                            amount = 1.0,
                            unit = "confezione",
                            lowStockThreshold = 2.0,
                        ),
                        expirationDate = LocalDate.of(2026, 7, 20),
                    ),
                    sampleMedicine(
                        id = MedicineId(4L),
                        name = "Senza scadenza",
                        expirationDate = null,
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val attentionItems = viewModel.uiState.value.attentionItems

        assertEquals(3, attentionItems.size)
        assertEquals(listOf("Scaduto", "In scadenza", "Senza scadenza"), attentionItems.map { it.name })
        assertEquals(MedicineStatusBadgeStatus.Expired, attentionItems[0].status)
        assertEquals(MedicineStatusBadgeStatus.ExpiringSoon, attentionItems[1].status)
        assertEquals(MedicineStatusBadgeStatus.NoExpiration, attentionItems[2].status)
    }

    /**
     * Verifica che una voce valida ma sotto soglia usi il badge UI dedicato alla Home.
     */
    @Test
    fun shouldExposeLowStockBadgeForValidMedicineBelowThreshold() = runTest {
        val viewModel = createViewModel(
            repository = FakeMedicineRepository(
                initialMedicines = listOf(
                    sampleMedicine(
                        name = "Scorta bassa",
                        quantity = QuantityInfo(
                            amount = 1.0,
                            unit = "confezione",
                            lowStockThreshold = 2.0,
                        ),
                        expirationDate = LocalDate.of(2026, 7, 20),
                    ),
                ),
            ),
        )

        advanceUntilIdle()

        val item = viewModel.uiState.value.attentionItems.single()

        assertEquals("Scorta bassa", item.name)
        assertEquals(MedicineStatusBadgeStatus.LowStock, item.status)
    }

    private fun createViewModel(repository: FakeMedicineRepository): HomeViewModel =
        HomeViewModel(
            getHomeSummaryUseCase = GetHomeSummaryUseCase(
                medicineRepository = repository,
                statusCalculator = MedicineStatusCalculator(
                    dateProvider = FakeDateProvider(LocalDate.of(2026, 6, 7)),
                ),
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
        expirationDate: LocalDate? = LocalDate.of(2026, 7, 20),
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
