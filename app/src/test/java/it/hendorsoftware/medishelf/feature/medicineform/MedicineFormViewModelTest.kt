package it.hendorsoftware.medishelf.feature.medicineform

import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.usecase.AddMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetMedicineByIdUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateMedicineUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test unitari per il ViewModel del form di inserimento medicinale.
 */
class MedicineFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica che il salvataggio con nome vuoto mostri errore vicino al campo.
     */
    @Test
    fun shouldShowNameErrorWhenSavingBlankMedicine() = runTest {
        val repository = FakeMedicineRepository()
        val viewModel = createViewModel(repository)

        viewModel.onNameChanged("   ")
        viewModel.onSaveClick()

        assertEquals(MedicineFormFieldError.REQUIRED, viewModel.uiState.value.nameError)
        assertTrue(repository.savedMedicines.isEmpty())
    }

    /**
     * Verifica che il ViewModel salvi una voce con il solo nome obbligatorio.
     */
    @Test
    fun shouldSaveMedicineWithOnlyName() = runTest {
        val repository = FakeMedicineRepository()
        val viewModel = createViewModel(repository)

        viewModel.onNameChanged("  Tachipirina  ")
        viewModel.onSaveClick()

        val savedMedicine = repository.savedMedicines.single()

        assertEquals("Tachipirina", savedMedicine.name)
        assertNull(savedMedicine.quantity)
        assertNull(savedMedicine.expirationDate)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    /**
     * Verifica che quantita e soglia valide vengano convertite in domain model.
     */
    @Test
    fun shouldSaveOptionalQuantityWhenProvided() = runTest {
        val repository = FakeMedicineRepository()
        val viewModel = createViewModel(repository)

        viewModel.onNameChanged("Ibuprofene")
        viewModel.onQuantityChanged("12,5")
        viewModel.onQuantityUnitChanged("compresse")
        viewModel.onLowStockThresholdChanged("2")
        viewModel.onSaveClick()

        val quantity = repository.savedMedicines.single().quantity

        assertEquals(12.5, quantity?.amount ?: 0.0, DOUBLE_DELTA)
        assertEquals("compresse", quantity?.unit)
        assertEquals(2.0, quantity?.lowStockThreshold ?: 0.0, DOUBLE_DELTA)
    }

    /**
     * Verifica che una scadenza non valida blocchi il salvataggio.
     */
    @Test
    fun shouldRejectInvalidExpirationDate() = runTest {
        val repository = FakeMedicineRepository()
        val viewModel = createViewModel(repository)

        viewModel.onNameChanged("Ibuprofene")
        viewModel.onExpirationDateChanged("18-05-2026")
        viewModel.onSaveClick()

        assertEquals(MedicineFormFieldError.INVALID_DATE, viewModel.uiState.value.expirationDateError)
        assertTrue(repository.savedMedicines.isEmpty())
    }

    /**
     * Verifica che il caricamento in modifica precompili tutti i campi salvati.
     */
    @Test
    fun shouldPrepopulateFieldsWhenEditingMedicine() = runTest {
        val medicine = sampleMedicine()
        val repository = FakeMedicineRepository(initialMedicines = listOf(medicine))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicineForEdit(medicine.id.value.toString())

        val uiState = viewModel.uiState.value

        assertEquals(MedicineFormMode.Edit, uiState.mode)
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isNotFound)
        assertEquals("Ibuprofene", uiState.name)
        assertEquals("Compresse", uiState.packageForm)
        assertEquals("12", uiState.quantity)
        assertEquals("compresse", uiState.quantityUnit)
        assertEquals("3", uiState.lowStockThreshold)
        assertEquals("2026-12-31", uiState.expirationDate)
        assertEquals("Bagno", uiState.storageLocation)
        assertEquals("Confezione iniziata", uiState.notes)
    }

    /**
     * Verifica che la modifica aggiorni la stessa voce senza cambiare id o data creazione.
     */
    @Test
    fun shouldUpdateExistingMedicineWhenSavingEditMode() = runTest {
        val medicine = sampleMedicine()
        val repository = FakeMedicineRepository(initialMedicines = listOf(medicine))
        val viewModel = createViewModel(repository)

        viewModel.loadMedicineForEdit(medicine.id.value.toString())
        viewModel.onNameChanged("  Ibuprofene aggiornato  ")
        viewModel.onQuantityChanged("")
        viewModel.onExpirationDateChanged("")
        viewModel.onSaveClick()

        val savedMedicine = repository.savedMedicines.single()

        assertEquals(medicine.id, savedMedicine.id)
        assertEquals(medicine.createdAt, savedMedicine.createdAt)
        assertEquals("Ibuprofene aggiornato", savedMedicine.name)
        assertNull(savedMedicine.quantity)
        assertNull(savedMedicine.expirationDate)
        assertTrue(viewModel.uiState.value.isSaved)
    }

    /**
     * Verifica che un id assente mostri lo stato dedicato senza salvare modifiche.
     */
    @Test
    fun shouldShowNotFoundStateWhenEditingMissingMedicine() = runTest {
        val repository = FakeMedicineRepository()
        val viewModel = createViewModel(repository)

        viewModel.loadMedicineForEdit("99")
        viewModel.onSaveClick()

        assertEquals(MedicineFormMode.Edit, viewModel.uiState.value.mode)
        assertTrue(viewModel.uiState.value.isNotFound)
        assertTrue(repository.savedMedicines.isEmpty())
    }

    private fun createViewModel(repository: FakeMedicineRepository): MedicineFormViewModel =
        MedicineFormViewModel(
            addMedicineUseCase = AddMedicineUseCase(repository),
            getMedicineByIdUseCase = GetMedicineByIdUseCase(repository),
            updateMedicineUseCase = UpdateMedicineUseCase(repository),
        )

    private fun sampleMedicine(): Medicine = Medicine(
        id = MedicineId(42L),
        name = "Ibuprofene",
        packageForm = "Compresse",
        quantity = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 3.0,
        ),
        expirationDate = LocalDate.parse("2026-12-31"),
        storageLocation = "Bagno",
        notes = "Confezione iniziata",
        isArchived = false,
        createdAt = Instant.parse("2026-05-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-05-10T10:00:00Z"),
        archivedAt = null,
    )

    private companion object {
        private const val DOUBLE_DELTA = 0.0001
    }
}
