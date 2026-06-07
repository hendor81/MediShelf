package it.hendorsoftware.medishelf.feature.home

import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus

/**
 * Stato immutabile della Home / Dashboard.
 *
 * @param isLoading indica se lo stream iniziale e ancora in caricamento.
 * @param activeMedicineCount totale dei medicinali attivi.
 * @param expiringMedicineCount medicinali in scadenza.
 * @param expiredMedicineCount medicinali scaduti.
 * @param lowStockMedicineCount medicinali con quantita nota a zero o sotto soglia.
 * @param attentionItems voci prioritarie mostrate nella sezione "Da tenere d'occhio".
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val activeMedicineCount: Int = 0,
    val expiringMedicineCount: Int = 0,
    val expiredMedicineCount: Int = 0,
    val lowStockMedicineCount: Int = 0,
    val attentionItems: List<HomeAttentionItemUiModel> = emptyList(),
) {

    /**
     * Indica se l'inventario attivo e vuoto.
     */
    val isEmpty: Boolean
        get() = !isLoading &&
            activeMedicineCount == 0 &&
            attentionItems.isEmpty()
}

/**
 * Modello UI compatto per una voce da tenere d'occhio nella Home.
 *
 * @param id identificativo usato per aprire il dettaglio.
 * @param name nome visibile del medicinale.
 * @param packageForm formato o confezione, se presente.
 * @param expirationDate data di scadenza formattata, se presente.
 * @param quantity quantita formattata, se presente.
 * @param status stato visuale calcolato dal dominio.
 */
data class HomeAttentionItemUiModel(
    val id: String,
    val name: String,
    val packageForm: String?,
    val expirationDate: String?,
    val quantity: String?,
    val status: MedicineStatusBadgeStatus,
)
