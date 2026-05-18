package it.hendorsoftware.medishelf.feature.medicineform

/**
 * Stato immutabile del form di inserimento o modifica medicinale.
 *
 * Mantiene solo valori pronti per la UI e segnala gli errori di validazione
 * vicino ai campi, senza esporre dettagli del dominio o della persistenza.
 */
data class MedicineFormUiState(
    val mode: MedicineFormMode = MedicineFormMode.Add,
    val name: String = "",
    val packageForm: String = "",
    val quantity: String = "",
    val quantityUnit: String = "",
    val lowStockThreshold: String = "",
    val expirationDate: String = "",
    val storageLocation: String = "",
    val notes: String = "",
    val nameError: MedicineFormFieldError? = null,
    val quantityError: MedicineFormFieldError? = null,
    val lowStockThresholdError: MedicineFormFieldError? = null,
    val expirationDateError: MedicineFormFieldError? = null,
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
)

/**
 * Modalita operativa del form condiviso tra creazione e modifica.
 */
enum class MedicineFormMode {
    Add,
    Edit,
}

/**
 * Errori di validazione mostrabili dalla schermata accanto al campo coinvolto.
 */
enum class MedicineFormFieldError {
    REQUIRED,
    INVALID_NUMBER,
    NEGATIVE_NUMBER,
    INVALID_DATE,
}
