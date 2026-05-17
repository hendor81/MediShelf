package it.hendorsoftware.medishelf.domain.model

/**
 * Quantita tracciata per un medicinale quando l'utente sceglie di indicarla.
 *
 * @param amount quantita disponibile; puo essere zero ma non negativa.
 * @param unit unita di misura o descrizione libera della quantita.
 * @param lowStockThreshold soglia opzionale sotto la quale la voce puo essere
 * considerata quasi terminata.
 *
 * @throws IllegalArgumentException se [amount] o [lowStockThreshold] sono negativi.
 */
data class QuantityInfo(
    val amount: Double,
    val unit: String?,
    val lowStockThreshold: Double?,
) {

    init {
        require(amount >= 0.0) { "Quantity amount cannot be negative." }
        require(lowStockThreshold == null || lowStockThreshold >= 0.0) {
            "Low stock threshold cannot be negative."
        }
    }
}
