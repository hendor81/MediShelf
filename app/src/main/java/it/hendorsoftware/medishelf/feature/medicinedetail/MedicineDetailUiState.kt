package it.hendorsoftware.medishelf.feature.medicinedetail

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus

/**
 * Stato immutabile della schermata Dettaglio medicinale.
 *
 * @param isLoading indica se il dettaglio e in fase di caricamento.
 * @param isNotFound indica che l'id richiesto non corrisponde a una voce locale.
 * @param medicine dati gia mappati per la visualizzazione, se disponibili.
 * @param isDeleteDialogVisible controlla la conferma obbligatoria di cancellazione.
 * @param isActionInProgress indica che archiviazione o cancellazione sono in corso.
 * @param hasArchiveCompleted segnala alla route che l'archiviazione e terminata.
 * @param hasDeleteCompleted segnala alla route che la cancellazione e terminata.
 * @param quantityFeedback feedback discreto da mostrare dopo azioni sulla quantita.
 */
data class MedicineDetailUiState(
    val isLoading: Boolean = true,
    val isNotFound: Boolean = false,
    val medicine: MedicineDetailUiModel? = null,
    val isDeleteDialogVisible: Boolean = false,
    val isActionInProgress: Boolean = false,
    val hasArchiveCompleted: Boolean = false,
    val hasDeleteCompleted: Boolean = false,
    val quantityFeedback: MedicineDetailQuantityFeedback? = null,
)

/**
 * Modello UI completo per il dettaglio di una voce dell'inventario.
 *
 * @param id identificativo usato dalle azioni di modifica e cancellazione.
 * @param name nome visibile del medicinale.
 * @param packageForm formato o confezione, mostrato solo se presente.
 * @param status stato visuale calcolato dal dominio o dedotto dall'archiviazione.
 * @param quantity quantita formattata, oppure null quando non indicata.
 * @param isQuantityAtZero indica se la quantita nota e gia pari a zero.
 * @param expirationDate data di scadenza formattata, oppure null quando assente.
 * @param storageLocation luogo di conservazione, oppure null quando assente.
 * @param notes note libere, oppure null quando assenti.
 * @param isArchived indica se la voce e gia fuori dall'inventario attivo.
 */
data class MedicineDetailUiModel(
    val id: String,
    val name: String,
    val packageForm: String?,
    val status: MedicineStatusBadgeStatus,
    val quantity: String?,
    val isQuantityAtZero: Boolean,
    val expirationDate: String?,
    val storageLocation: String?,
    val notes: String?,
    val isArchived: Boolean,
)

/**
 * Feedback utente per le azioni rapide sulla quantita.
 */
enum class MedicineDetailQuantityFeedback {
    Updated,
    MissingQuantity,
    AlreadyZero,
}
