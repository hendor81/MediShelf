package it.hendorsoftware.medishelf.feature.medicineform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.usecase.AddMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetMedicineByIdUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateMedicineUseCase
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del form di inserimento e modifica medicinale.
 *
 * Coordina validazione leggera dei campi UI, conversione dei valori opzionali
 * e salvataggio tramite use case, mantenendo Room fuori dal feature layer.
 */
@HiltViewModel
class MedicineFormViewModel @Inject constructor(
    private val addMedicineUseCase: AddMedicineUseCase,
    private val getMedicineByIdUseCase: GetMedicineByIdUseCase,
    private val updateMedicineUseCase: UpdateMedicineUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineFormUiState())
    private var currentMedicine: Medicine? = null

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente del form.
     */
    val uiState: StateFlow<MedicineFormUiState> = _uiState.asStateFlow()

    /**
     * Carica un medicinale esistente e precompila il form in modalita modifica.
     *
     * @param medicineId identificativo ricevuto dalla route di navigazione.
     */
    fun loadMedicineForEdit(medicineId: String) {
        val id = medicineId.toLongOrNull()?.let(::MedicineId)

        if (id == null) {
            currentMedicine = null
            _uiState.value = MedicineFormUiState(
                mode = MedicineFormMode.Edit,
                isNotFound = true,
            )
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    mode = MedicineFormMode.Edit,
                    isLoading = true,
                    isNotFound = false,
                    isSaved = false,
                )
            }

            val medicine = getMedicineByIdUseCase(id)
            currentMedicine = medicine

            _uiState.value = medicine?.toEditUiState()
                ?: MedicineFormUiState(
                    mode = MedicineFormMode.Edit,
                    isLoading = false,
                    isNotFound = true,
                )
        }
    }

    /**
     * Aggiorna il nome obbligatorio e rimuove l'errore se il valore torna valido.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onNameChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                name = value,
                nameError = state.nameError.takeUnless { value.isNotBlank() },
                isSaved = false,
            )
        }
    }

    /**
     * Aggiorna il formato o confezione opzionale.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onPackageFormChanged(value: String) {
        _uiState.update { state -> state.copy(packageForm = value, isSaved = false) }
    }

    /**
     * Aggiorna la quantita opzionale e rimuove errori precedenti mentre l'utente corregge.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onQuantityChanged(value: String) {
        _uiState.update { state ->
            state.copy(quantity = value, quantityError = null, isSaved = false)
        }
    }

    /**
     * Aggiorna l'unita libera della quantita.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onQuantityUnitChanged(value: String) {
        _uiState.update { state -> state.copy(quantityUnit = value, isSaved = false) }
    }

    /**
     * Aggiorna la soglia opzionale di scorta bassa.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onLowStockThresholdChanged(value: String) {
        _uiState.update { state ->
            state.copy(lowStockThreshold = value, lowStockThresholdError = null, isSaved = false)
        }
    }

    /**
     * Aggiorna la data di scadenza opzionale nel formato ISO `yyyy-MM-dd`.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onExpirationDateChanged(value: String) {
        _uiState.update { state ->
            state.copy(expirationDate = value, expirationDateError = null, isSaved = false)
        }
    }

    /**
     * Aggiorna il luogo di conservazione libero.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onStorageLocationChanged(value: String) {
        _uiState.update { state -> state.copy(storageLocation = value, isSaved = false) }
    }

    /**
     * Aggiorna le note libere opzionali.
     *
     * @param value nuovo testo inserito dall'utente.
     */
    fun onNotesChanged(value: String) {
        _uiState.update { state -> state.copy(notes = value, isSaved = false) }
    }

    /**
     * Valida i campi e salva il medicinale quando i dati sono coerenti.
     *
     * La quantita e la scadenza restano opzionali: valori vuoti vengono salvati
     * come assenti e non bloccano ne inserimento ne modifica.
     */
    fun onSaveClick() {
        val currentState = _uiState.value
        val validationResult = currentState.validate()

        if (!validationResult.isValid) {
            _uiState.value = validationResult.state
            return
        }

        viewModelScope.launch {
            _uiState.update { state -> state.copy(isSaving = true, isSaved = false) }

            val validatedState = _uiState.value
            val wasSaved = when (validatedState.mode) {
                MedicineFormMode.Add -> {
                    addMedicine(validatedState)
                    true
                }
                MedicineFormMode.Edit -> updateMedicine(validatedState)
            }

            if (wasSaved) {
                _uiState.update { state -> state.copy(isSaving = false, isSaved = true) }
            }
        }
    }

    private suspend fun addMedicine(state: MedicineFormUiState) {
        addMedicineUseCase(
            name = state.name,
            packageForm = state.packageForm.trimToNull(),
            quantity = state.toQuantityInfo(),
            expirationDate = state.expirationDate.trimToNull()?.let(LocalDate::parse),
            storageLocation = state.storageLocation.trimToNull(),
            notes = state.notes.trimToNull(),
        )
    }

    private suspend fun updateMedicine(state: MedicineFormUiState): Boolean {
        val medicine = currentMedicine

        if (medicine == null) {
            _uiState.update { currentState ->
                currentState.copy(isSaving = false, isNotFound = true)
            }
            return false
        }

        val updatedMedicine = medicine.copy(
            name = state.name,
            packageForm = state.packageForm.trimToNull(),
            quantity = state.toQuantityInfo(),
            expirationDate = state.expirationDate.trimToNull()?.let(LocalDate::parse),
            storageLocation = state.storageLocation.trimToNull(),
            notes = state.notes.trimToNull(),
        )

        currentMedicine = updateMedicineUseCase(updatedMedicine)
        return true
    }

    private fun Medicine.toEditUiState(): MedicineFormUiState = MedicineFormUiState(
        mode = MedicineFormMode.Edit,
        name = name,
        packageForm = packageForm.orEmpty(),
        quantity = quantity?.amount?.toInputText().orEmpty(),
        quantityUnit = quantity?.unit.orEmpty(),
        lowStockThreshold = quantity?.lowStockThreshold?.toInputText().orEmpty(),
        expirationDate = expirationDate?.toString().orEmpty(),
        storageLocation = storageLocation.orEmpty(),
        notes = notes.orEmpty(),
        isLoading = false,
        isNotFound = false,
        isSaving = false,
        isSaved = false,
    )

    private fun MedicineFormUiState.validate(): ValidationResult {
        val quantityValidation = quantity.parseOptionalNonNegativeDouble()
        val lowStockThresholdValidation = lowStockThreshold.parseOptionalNonNegativeDouble()
        val expirationDateValidation = expirationDate.parseOptionalDate()

        val validatedState = copy(
            nameError = if (name.isBlank()) MedicineFormFieldError.REQUIRED else null,
            quantityError = quantityValidation.error,
            lowStockThresholdError = lowStockThresholdValidation.error,
            expirationDateError = expirationDateValidation,
            isSaved = false,
        )

        return ValidationResult(
            state = validatedState,
            isValid = listOf(
                validatedState.nameError,
                validatedState.quantityError,
                validatedState.lowStockThresholdError,
                validatedState.expirationDateError,
            ).all { error -> error == null },
        )
    }

    private fun MedicineFormUiState.toQuantityInfo(): QuantityInfo? {
        val amount = quantity.parseOptionalNonNegativeDouble().value ?: return null
        val threshold = lowStockThreshold.parseOptionalNonNegativeDouble().value

        return QuantityInfo(
            amount = amount,
            unit = quantityUnit.trimToNull(),
            lowStockThreshold = threshold,
        )
    }

    private fun String.trimToNull(): String? = trim().takeIf(String::isNotEmpty)

    private fun Double.toInputText(): String =
        if (this % WHOLE_NUMBER_DIVISOR == 0.0) {
            toLong().toString()
        } else {
            toString()
        }

    private fun String.parseOptionalNonNegativeDouble(): OptionalNumberValidation {
        val normalizedValue = trim().replace(',', '.')

        if (normalizedValue.isEmpty()) {
            return OptionalNumberValidation(value = null, error = null)
        }

        val parsedValue = normalizedValue.toDoubleOrNull()
            ?: return OptionalNumberValidation(
                value = null,
                error = MedicineFormFieldError.INVALID_NUMBER,
            )

        return if (parsedValue < 0.0) {
            OptionalNumberValidation(value = null, error = MedicineFormFieldError.NEGATIVE_NUMBER)
        } else {
            OptionalNumberValidation(value = parsedValue, error = null)
        }
    }

    private fun String.parseOptionalDate(): MedicineFormFieldError? {
        val normalizedValue = trim()

        if (normalizedValue.isEmpty()) {
            return null
        }

        return try {
            LocalDate.parse(normalizedValue)
            null
        } catch (_: DateTimeParseException) {
            MedicineFormFieldError.INVALID_DATE
        }
    }

    private data class OptionalNumberValidation(
        val value: Double?,
        val error: MedicineFormFieldError?,
    )

    private data class ValidationResult(
        val state: MedicineFormUiState,
        val isValid: Boolean,
    )

    private companion object {
        private const val WHOLE_NUMBER_DIVISOR = 1.0
    }
}
