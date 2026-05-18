package it.hendorsoftware.medishelf.feature.medicineform

import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.usecase.AddMedicineUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        val viewModel = MedicineFormViewModel(AddMedicineUseCase(repository))

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
        val viewModel = MedicineFormViewModel(AddMedicineUseCase(repository))

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
        val viewModel = MedicineFormViewModel(AddMedicineUseCase(repository))

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
        val viewModel = MedicineFormViewModel(AddMedicineUseCase(repository))

        viewModel.onNameChanged("Ibuprofene")
        viewModel.onExpirationDateChanged("18-05-2026")
        viewModel.onSaveClick()

        assertEquals(MedicineFormFieldError.INVALID_DATE, viewModel.uiState.value.expirationDateError)
        assertTrue(repository.savedMedicines.isEmpty())
    }

    private companion object {
        private const val DOUBLE_DELTA = 0.0001
    }
}
