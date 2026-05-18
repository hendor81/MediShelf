package it.hendorsoftware.medishelf.domain.repository

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repository fake in memoria per test unitari di use case e ViewModel.
 *
 * @param initialMedicines medicinali iniziali pubblicati dagli stream osservabili.
 * @param archivedAt istante deterministico usato quando un test archivia una voce.
 *
 * Il fake mantiene il comportamento minimo del repository domain senza accedere
 * a Room, cosi i test possono verificare Flow, salvataggi e archiviazione in modo
 * rapido e deterministico.
 */
class FakeMedicineRepository(
    initialMedicines: List<Medicine> = emptyList(),
    private val archivedAt: Instant = DEFAULT_ARCHIVED_AT,
) : MedicineRepository {

    private val medicines = MutableStateFlow(initialMedicines)

    val savedMedicines = mutableListOf<Medicine>()
    val archivedMedicineIds = mutableListOf<MedicineId>()
    val deletedMedicineIds = mutableListOf<MedicineId>()

    /**
     * Osserva solo le voci non archiviate presenti nello stato in memoria.
     *
     * @return stream filtrato dei medicinali attivi.
     */
    override fun observeActiveMedicines(): Flow<List<Medicine>> =
        medicines.map { values -> values.filterNot(Medicine::isArchived) }

    /**
     * Osserva solo le voci archiviate presenti nello stato in memoria.
     *
     * @return stream filtrato dei medicinali archiviati.
     */
    override fun observeArchivedMedicines(): Flow<List<Medicine>> =
        medicines.map { values -> values.filter(Medicine::isArchived) }

    /**
     * Cerca una voce nello stato in memoria tramite identificativo domain.
     *
     * @param id identificativo della voce richiesta.
     * @return medicinale trovato, oppure null.
     */
    override suspend fun getMedicineById(id: MedicineId): Medicine? =
        medicines.value.firstOrNull { medicine -> medicine.id == id }

    /**
     * Registra il salvataggio richiesto e aggiorna lo stato osservabile.
     *
     * @param medicine medicinale da aggiungere o sostituire nello stato fake.
     */
    override suspend fun saveMedicine(medicine: Medicine) {
        savedMedicines += medicine
        upsertMedicine(medicine)
    }

    /**
     * Marca come archiviata la voce presente nello stato in memoria.
     *
     * @param id identificativo della voce da archiviare.
     */
    override suspend fun archiveMedicine(id: MedicineId) {
        archivedMedicineIds += id
        medicines.value = medicines.value.map { medicine ->
            if (medicine.id == id) {
                medicine.copy(
                    isArchived = true,
                    updatedAt = archivedAt,
                    archivedAt = archivedAt,
                )
            } else {
                medicine
            }
        }
    }

    /**
     * Rimuove definitivamente la voce dallo stato in memoria.
     *
     * @param id identificativo della voce da eliminare.
     */
    override suspend fun deleteMedicine(id: MedicineId) {
        deletedMedicineIds += id
        medicines.value = medicines.value.filterNot { medicine -> medicine.id == id }
    }

    /**
     * Sostituisce l'intero stato pubblicato dagli stream del fake.
     *
     * @param values nuova lista di medicinali da esporre ai test.
     */
    fun setMedicines(values: List<Medicine>) {
        medicines.value = values
    }

    /**
     * Restituisce una fotografia dello stato corrente in memoria.
     *
     * @return medicinali attualmente contenuti nel fake.
     */
    fun currentMedicines(): List<Medicine> = medicines.value

    private fun upsertMedicine(medicine: Medicine) {
        val currentMedicines = medicines.value.toMutableList()
        val currentIndex = currentMedicines.indexOfFirst { current ->
            current.id == medicine.id
        }

        if (currentIndex >= 0) {
            currentMedicines[currentIndex] = medicine
        } else {
            currentMedicines += medicine
        }

        medicines.value = currentMedicines
    }

    private companion object {
        private val DEFAULT_ARCHIVED_AT: Instant = Instant.parse("2026-05-18T12:00:00Z")
    }
}
