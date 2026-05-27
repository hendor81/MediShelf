package it.hendorsoftware.medishelf.feature.inventory

import androidx.annotation.StringRes
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.domain.model.MedicineStatus

/**
 * Filtro UI applicabile alla lista dei medicinali attivi in base allo stato calcolato.
 *
 * @param labelResId risorsa testuale mostrata nel controllo filtro.
 * @param medicineStatus stato di dominio associato al filtro, oppure null per mostrare tutto.
 */
enum class InventoryStatusFilter(
    @param:StringRes val labelResId: Int,
    val medicineStatus: MedicineStatus?,
) {
    /**
     * Mostra tutti i medicinali attivi, indipendentemente dallo stato.
     */
    All(
        labelResId = R.string.inventory_status_filter_all,
        medicineStatus = null,
    ),

    /**
     * Mostra solo medicinali validi.
     */
    Valid(
        labelResId = R.string.medicine_status_valid,
        medicineStatus = MedicineStatus.VALID,
    ),

    /**
     * Mostra solo medicinali in scadenza.
     */
    ExpiringSoon(
        labelResId = R.string.medicine_status_expiring_soon,
        medicineStatus = MedicineStatus.EXPIRING,
    ),

    /**
     * Mostra solo medicinali scaduti.
     */
    Expired(
        labelResId = R.string.medicine_status_expired,
        medicineStatus = MedicineStatus.EXPIRED,
    ),

    /**
     * Mostra solo medicinali esauriti.
     */
    OutOfStock(
        labelResId = R.string.medicine_status_out_of_stock,
        medicineStatus = MedicineStatus.OUT_OF_STOCK,
    ),

    /**
     * Mostra solo medicinali senza scadenza indicata.
     */
    NoExpirationDate(
        labelResId = R.string.medicine_status_no_expiration,
        medicineStatus = MedicineStatus.NO_EXPIRATION_DATE,
    );

    /**
     * Verifica se uno stato calcolato deve essere incluso dal filtro corrente.
     *
     * @param status stato calcolato dal dominio per il medicinale.
     * @return true se il filtro include il medicinale.
     */
    fun includes(status: MedicineStatus): Boolean =
        medicineStatus == null || medicineStatus == status
}
