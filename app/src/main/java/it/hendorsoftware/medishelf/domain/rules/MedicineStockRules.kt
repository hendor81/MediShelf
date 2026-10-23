package it.hendorsoftware.medishelf.domain.rules

import it.hendorsoftware.medishelf.domain.model.Medicine

/** Regole condivise delle scorte: esaurito e scorta bassa restano condizioni distinte. */
object MedicineStockRules {
    /** Quantità nota positiva entro la soglia; le voci archiviate non richiedono attenzione. */
    fun isLowStock(medicine: Medicine): Boolean {
        val quantity = medicine.quantity ?: return false
        val threshold = quantity.lowStockThreshold ?: return false
        return !medicine.isArchived && quantity.amount > 0.0 && quantity.amount <= threshold
    }
}
