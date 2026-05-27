package it.hendorsoftware.medishelf.feature.archive

/**
 * Stato immutabile della schermata Archivio.
 *
 * @param isLoading indica se la lista sta osservando il primo caricamento.
 * @param medicines medicinali archiviati gia pronti per la visualizzazione.
 */
data class ArchiveUiState(
    val isLoading: Boolean = true,
    val medicines: List<ArchiveMedicineItemUiModel> = emptyList(),
)

/**
 * Modello UI compatto per una voce archiviata.
 *
 * @param id identificativo usato dalla navigazione verso il dettaglio.
 * @param name nome visibile del medicinale.
 * @param packageForm formato o confezione, mostrato solo se presente.
 * @param expirationDate data di scadenza formattata, se presente.
 * @param quantity quantita formattata, se presente.
 * @param storageLocation luogo di conservazione, se presente.
 */
data class ArchiveMedicineItemUiModel(
    val id: String,
    val name: String,
    val packageForm: String?,
    val expirationDate: String?,
    val quantity: String?,
    val storageLocation: String?,
)
