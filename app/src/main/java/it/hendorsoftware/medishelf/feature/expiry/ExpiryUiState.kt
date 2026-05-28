package it.hendorsoftware.medishelf.feature.expiry

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus

/**
 * Stato immutabile della schermata Scadenzario.
 *
 * @param isLoading indica se lo stream iniziale dei medicinali e ancora in caricamento.
 * @param expiringMedicines medicinali attivi con scadenza entro la soglia configurata.
 * @param expiredMedicines medicinali attivi con data di scadenza gia superata.
 * @param noExpirationMedicines medicinali attivi senza data di scadenza indicata.
 */
data class ExpiryUiState(
    val isLoading: Boolean = true,
    val expiringMedicines: List<ExpiryMedicineItemUiModel> = emptyList(),
    val expiredMedicines: List<ExpiryMedicineItemUiModel> = emptyList(),
    val noExpirationMedicines: List<ExpiryMedicineItemUiModel> = emptyList(),
) {

    /**
     * Indica se lo scadenzario non contiene alcuna voce da mostrare.
     */
    val isEmpty: Boolean
        get() = expiringMedicines.isEmpty() &&
            expiredMedicines.isEmpty() &&
            noExpirationMedicines.isEmpty()
}

/**
 * Modello UI compatto per una voce dello Scadenzario.
 *
 * @param id identificativo usato dalla navigazione verso il dettaglio.
 * @param name nome visibile del medicinale.
 * @param packageForm formato o confezione, mostrato solo se presente.
 * @param status stato visuale calcolato dal dominio.
 * @param expirationDate data di scadenza formattata, se presente.
 * @param quantity quantita formattata, se presente.
 * @param storageLocation luogo di conservazione, se presente.
 */
data class ExpiryMedicineItemUiModel(
    val id: String,
    val name: String,
    val packageForm: String?,
    val status: MedicineStatusBadgeStatus,
    val expirationDate: String?,
    val quantity: String?,
    val storageLocation: String?,
)
