package it.hendorsoftware.medishelf.feature.medicineform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.usecase.AddMedicineUseCase
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del form di inserimento medicinale.
 *
 * Coordina validazione leggera dei campi UI, conversione dei valori opzionali
 * e salvataggio tramite [AddMedicineUseCase], mantenendo Room fuori dalla
 * feature layer.
 */
@HiltViewModel
class MedicineFormViewModel @Inject constructor(
    private val addMedicineUseCase: AddMedicineUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineFormUiState())

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente del form.
     */
    val uiState: StateFlow<MedicineFormUiState> = _uiState.asStateFlow()

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
     * Valida i campi e salva un nuovo medicinale quando i dati sono coerenti.
     *
     * La quantita e la scadenza restano opzionali: valori vuoti vengono salvati
     * come assenti e non bloccano il flusso di inserimento.
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
            addMedicineUseCase(
                name = validatedState.name,
                packageForm = validatedState.packageForm.trimToNull(),
                quantity = validatedState.toQuantityInfo(),
                expirationDate = validatedState.expirationDate.trimToNull()?.let(LocalDate::parse),
                storageLocation = validatedState.storageLocation.trimToNull(),
                notes = validatedState.notes.trimToNull(),
            )

            _uiState.update { state -> state.copy(isSaving = false, isSaved = true) }
        }
    }

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
}
