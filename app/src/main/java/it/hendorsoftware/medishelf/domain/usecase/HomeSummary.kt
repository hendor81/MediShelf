package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus

/**
 * Riepilogo di dominio usato dalla Home / Dashboard.
 *
 * @param activeMedicineCount numero totale di medicinali non archiviati.
 * @param expiringMedicineCount numero di medicinali attivi in scadenza.
 * @param expiredMedicineCount numero di medicinali attivi gia scaduti.
 * @param lowStockMedicineCount numero di medicinali con quantita nota a zero o sotto soglia.
 * @param attentionMedicines voci prioritarie da proporre nella sezione di attenzione.
 */
data class HomeSummary(
    val activeMedicineCount: Int,
    val expiringMedicineCount: Int,
    val expiredMedicineCount: Int,
    val lowStockMedicineCount: Int,
    val attentionMedicines: List<HomeAttentionMedicine>,
)

/**
 * Medicinale con stato calcolato da mostrare tra le priorita della Home.
 *
 * @param medicine voce di dominio selezionata.
 * @param status stato derivato usato per ordinamento e badge.
 * @param isLowStock indica se la voce ha quantita nota a zero o sotto soglia.
 */
data class HomeAttentionMedicine(
    val medicine: Medicine,
    val status: MedicineStatus,
    val isLowStock: Boolean,
)
