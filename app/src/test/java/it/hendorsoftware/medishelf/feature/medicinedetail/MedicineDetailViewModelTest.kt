package it.hendorsoftware.medishelf.feature.medicinedetail

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.time.FakeDateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.ArchiveMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.DeleteMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetMedicineByIdUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateMedicineQuantityUseCase
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
 * Test unitari per il ViewModel del dettaglio medicinale.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MedicineDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica che il dettaglio carichi e formatti i dati principali del medicinale.
     */
    @Test
    fun shouldLoadMedicineDetail() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(sampleMedicine()))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        val medicine = uiState.medicine

        assertFalse(uiState.isLoading)
        assertFalse(uiState.isNotFound)
        assertEquals("1", medicine?.id)
        assertEquals("Paracetamolo", medicine?.name)
        assertEquals("Compresse", medicine?.packageForm)
        assertEquals(MedicineStatusBadgeStatus.ExpiringSoon, medicine?.status)
        assertEquals("12 compresse", medicine?.quantity)
        assertEquals("31/05/2026", medicine?.expirationDate)
        assertEquals("Bagno", medicine?.storageLocation)
        assertEquals("Confezione iniziata", medicine?.notes)
    }

    /**
     * Verifica che un id assente esponga lo stato dedicato senza dati sporchi.
     */
    @Test
    fun shouldShowNotFoundWhenMedicineDoesNotExist() = runTest {
        val viewModel = createViewModel(FakeMedicineRepository())

        viewModel.loadMedicine("99")
        advanceUntilIdle()

        val uiState = viewModel.uiState.value

        assertFalse(uiState.isLoading)
        assertTrue(uiState.isNotFound)
        assertNull(uiState.medicine)
    }

    /**
     * Verifica che campi opzionali mancanti restino null per la UI neutra.
     */
    @Test
    fun shouldMapMissingQuantityAndExpirationAsNull() = runTest {
        val repository = FakeMedicineRepository(
            initialMedicines = listOf(
                sampleMedicine(
                    quantity = null,
                    expirationDate = null,
                    storageLocation = null,
                    notes = null,
                ),
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()

        val medicine = viewModel.uiState.value.medicine

        assertEquals(MedicineStatusBadgeStatus.NoExpiration, medicine?.status)
        assertNull(medicine?.quantity)
        assertNull(medicine?.expirationDate)
        assertNull(medicine?.storageLocation)
        assertNull(medicine?.notes)
    }

    /**
     * Verifica che l'azione rapida incrementi una quantita gia presente.
     */
    @Test
    fun shouldIncrementQuantityWhenPresent() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(sampleMedicine()))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()
        viewModel.onQuantityIncrementClick()
        advanceUntilIdle()

        val savedMedicine = repository.savedMedicines.single()

        assertEquals(13.0, requireNotNull(savedMedicine.quantity).amount, DOUBLE_DELTA)
        assertEquals("13 compresse", viewModel.uiState.value.medicine?.quantity)
        assertEquals(MedicineDetailQuantityFeedback.Updated, viewModel.uiState.value.quantityFeedback)
    }

    /**
     * Verifica che l'azione rapida decrementi senza perdere unita e soglia.
     */
    @Test
    fun shouldDecrementQuantityWhenPresent() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(sampleMedicine()))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()
        viewModel.onQuantityDecrementClick()
        advanceUntilIdle()

        val savedMedicine = repository.savedMedicines.single()
        val quantity = requireNotNull(savedMedicine.quantity)

        assertEquals(11.0, quantity.amount, DOUBLE_DELTA)
        assertEquals("compresse", quantity.unit)
        assertEquals(2.0, requireNotNull(quantity.lowStockThreshold), DOUBLE_DELTA)
        assertEquals("11 compresse", viewModel.uiState.value.medicine?.quantity)
        assertEquals(MedicineDetailQuantityFeedback.Updated, viewModel.uiState.value.quantityFeedback)
    }

    /**
     * Verifica che la quantita assente non venga sostituita con un valore fittizio.
     */
    @Test
    fun shouldNotCreateQuantityWhenMissing() = runTest {
        val repository = FakeMedicineRepository(
            initialMedicines = listOf(sampleMedicine(quantity = null)),
        )
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()
        viewModel.onQuantityIncrementClick()
        advanceUntilIdle()

        assertTrue(repository.savedMedicines.isEmpty())
        assertNull(repository.currentMedicines().single().quantity)
        assertNull(viewModel.uiState.value.medicine?.quantity)
        assertEquals(
            MedicineDetailQuantityFeedback.MissingQuantity,
            viewModel.uiState.value.quantityFeedback,
        )
    }

    /**
     * Verifica che il decremento a zero non produca valori negativi.
     */
    @Test
    fun shouldKeepQuantityAtZeroWhenDecrementingZero() = runTest {
        val repository = FakeMedicineRepository(
            initialMedicines = listOf(
                sampleMedicine(
                    quantity = QuantityInfo(
                        amount = 0.0,
                        unit = "compresse",
                        lowStockThreshold = 2.0,
                    ),
                ),
            ),
        )
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()
        viewModel.onQuantityDecrementClick()
        advanceUntilIdle()

        val medicine = viewModel.uiState.value.medicine

        assertTrue(repository.savedMedicines.isEmpty())
        assertEquals(0.0, requireNotNull(repository.currentMedicines().single().quantity).amount, DOUBLE_DELTA)
        assertEquals("0 compresse", medicine?.quantity)
        assertTrue(medicine?.isQuantityAtZero == true)
        assertEquals(MedicineDetailQuantityFeedback.AlreadyZero, viewModel.uiState.value.quantityFeedback)
    }

    /**
     * Verifica che l'archiviazione sia distinta dalla cancellazione definitiva.
     */
    @Test
    fun shouldArchiveMedicineWithoutDeletingIt() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(sampleMedicine()))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()
        viewModel.onArchiveClick()
        advanceUntilIdle()

        assertEquals(listOf(MedicineId(1L)), repository.archivedMedicineIds)
        assertTrue(repository.deletedMedicineIds.isEmpty())
        assertTrue(viewModel.uiState.value.hasArchiveCompleted)
        assertTrue(viewModel.uiState.value.medicine?.isArchived == true)
    }

    /**
     * Verifica che la cancellazione venga eseguita solo dopo conferma esplicita.
     */
    @Test
    fun shouldDeleteMedicineOnlyAfterConfirmation() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(sampleMedicine()))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicine("1")
        advanceUntilIdle()
        viewModel.onDeleteClick()

        assertTrue(viewModel.uiState.value.isDeleteDialogVisible)
        assertTrue(repository.deletedMedicineIds.isEmpty())

        viewModel.onDeleteConfirmed()
        advanceUntilIdle()

        assertEquals(listOf(MedicineId(1L)), repository.deletedMedicineIds)
        assertTrue(viewModel.uiState.value.hasDeleteCompleted)
    }

    private fun createViewModel(repository: FakeMedicineRepository): MedicineDetailViewModel =
        MedicineDetailViewModel(
            getMedicineByIdUseCase = GetMedicineByIdUseCase(repository),
            archiveMedicineUseCase = ArchiveMedicineUseCase(repository),
            deleteMedicineUseCase = DeleteMedicineUseCase(repository),
            updateMedicineQuantityUseCase = UpdateMedicineQuantityUseCase(repository),
            observeUserSettingsUseCase = ObserveUserSettingsUseCase(FakeUserSettingsRepository()),
            statusCalculator = MedicineStatusCalculator(
                dateProvider = FakeDateProvider(LocalDate.of(2026, 5, 18)),
            ),
        )

    private fun sampleMedicine(
        quantity: QuantityInfo? = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        ),
        expirationDate: LocalDate? = LocalDate.of(2026, 5, 31),
        storageLocation: String? = "Bagno",
        notes: String? = "Confezione iniziata",
    ): Medicine = Medicine(
        id = MedicineId(1L),
        name = "Paracetamolo",
        packageForm = "Compresse",
        quantity = quantity,
        expirationDate = expirationDate,
        storageLocation = storageLocation,
        notes = notes,
        isArchived = false,
        createdAt = Instant.parse("2026-05-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-05-10T10:00:00Z"),
        archivedAt = null,
    )

    private companion object {
        private const val DOUBLE_DELTA = 0.0
    }
}
