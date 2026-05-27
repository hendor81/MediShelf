package it.hendorsoftware.medishelf.feature.inventory

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus

/**
 * Stato immutabile della schermata Inventario.
 *
 * @param isLoading indica se la lista sta osservando il primo caricamento.
 * @param medicines elementi attivi gia pronti per la visualizzazione.
 * @param searchQuery testo usato per filtrare i medicinali per nome.
 * @param selectedStatusFilter filtro di stato selezionato dall'utente.
 * @param hasActiveMedicines indica se l'inventario contiene almeno una voce attiva prima della ricerca.
 */
data class InventoryUiState(
    val isLoading: Boolean = true,
    val medicines: List<InventoryMedicineItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val selectedStatusFilter: InventoryStatusFilter = InventoryStatusFilter.All,
    val hasActiveMedicines: Boolean = false,
)

/**
 * Modello UI compatto per una voce della lista Inventario.
 *
 * @param id identificativo usato dalla navigazione verso il dettaglio.
 * @param name nome visibile del medicinale.
 * @param packageForm formato o confezione, mostrato solo se presente.
 * @param status stato visuale calcolato dal dominio.
 * @param expirationDate data di scadenza formattata, se presente.
 * @param quantity quantita formattata, se presente.
 * @param storageLocation luogo di conservazione, se presente.
 */
data class InventoryMedicineItemUiModel(
    val id: String,
    val name: String,
    val packageForm: String?,
    val status: MedicineStatusBadgeStatus,
    val expirationDate: String?,
    val quantity: String?,
    val storageLocation: String?,
)
