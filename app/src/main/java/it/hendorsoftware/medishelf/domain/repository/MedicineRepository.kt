package it.hendorsoftware.medishelf.domain.repository

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import kotlinx.coroutines.flow.Flow

/**
 * Contratto di accesso ai medicinali esposto al dominio.
 *
 * Il domain conosce solo questo contratto e i modelli di dominio: i dettagli
 * Room, le entity persistenti e i mapper restano confinati nel layer data.
 */
interface MedicineRepository {

    /**
     * Osserva i medicinali presenti nell'inventario attivo.
     *
     * @return stream dei medicinali non archiviati, gia convertiti in modello domain.
     */
    fun observeActiveMedicines(): Flow<List<Medicine>>

    /**
     * Osserva i medicinali spostati in archivio.
     *
     * @return stream dei medicinali archiviati, gia convertiti in modello domain.
     */
    fun observeArchivedMedicines(): Flow<List<Medicine>>

    /**
     * Recupera un medicinale tramite identificativo locale.
     *
     * @param id identificativo domain della voce cercata.
     * @return medicinale trovato, oppure null se non esiste.
     */
    suspend fun getMedicineById(id: MedicineId): Medicine?

    /**
     * Salva una nuova voce o aggiorna una voce esistente.
     *
     * @param medicine modello domain da persistere nel database locale.
     */
    suspend fun saveMedicine(medicine: Medicine)

    /**
     * Archivia logicamente un medicinale senza cancellarlo dal database.
     *
     * @param id identificativo domain della voce da archiviare.
     */
    suspend fun archiveMedicine(id: MedicineId)

    /**
     * Elimina definitivamente un medicinale gia confermato dall'utente.
     *
     * @param id identificativo domain della voce da rimuovere.
     */
    suspend fun deleteMedicine(id: MedicineId)
}
