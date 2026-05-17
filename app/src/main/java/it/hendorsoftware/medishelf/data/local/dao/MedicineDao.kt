package it.hendorsoftware.medishelf.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.hendorsoftware.medishelf.data.local.entity.MedicineEntity
import java.time.Instant
import kotlinx.coroutines.flow.Flow

/**
 * DAO Room per leggere e modificare i medicinali persistiti localmente.
 *
 * Le query distinguono esplicitamente inventario attivo e archivio, mantenendo
 * la cancellazione fisica come operazione separata dall'archiviazione logica.
 */
@Dao
interface MedicineDao {

    /**
     * Osserva le voci non archiviate ordinate per nome.
     *
     * @return stream dei medicinali attivi presenti nel database.
     */
    @Query("SELECT * FROM medicines WHERE isArchived = 0 ORDER BY name COLLATE NOCASE ASC")
    fun observeActiveMedicines(): Flow<List<MedicineEntity>>

    /**
     * Osserva le voci archiviate, mostrando prima quelle archiviate o aggiornate piu di recente.
     *
     * @return stream dei medicinali esclusi dall'inventario attivo.
     */
    @Query(
        """
        SELECT * FROM medicines
        WHERE isArchived = 1
        ORDER BY COALESCE(archivedAt, updatedAt) DESC, name COLLATE NOCASE ASC
        """,
    )
    fun observeArchivedMedicines(): Flow<List<MedicineEntity>>

    /**
     * Recupera una voce tramite identificativo tecnico locale.
     *
     * @param id identificativo Room del medicinale.
     * @return entity trovata, oppure null se non esiste.
     */
    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineById(id: Long): MedicineEntity?

    /**
     * Inserisce una nuova voce e restituisce l'identificativo generato da Room.
     *
     * @param entity entity da salvare.
     * @return id locale generato.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(entity: MedicineEntity): Long

    /**
     * Aggiorna una voce esistente mantenendo invariato il suo identificativo.
     *
     * @param entity entity completa con i nuovi valori persistenti.
     */
    @Update
    suspend fun updateMedicine(entity: MedicineEntity)

    /**
     * Marca una voce come archiviata senza cancellarla fisicamente dal database.
     *
     * @param id identificativo della voce da archiviare.
     * @param archivedAt istante in cui la voce viene spostata in archivio.
     */
    @Query(
        """
        UPDATE medicines
        SET isArchived = 1,
            archivedAt = :archivedAt,
            updatedAt = :archivedAt
        WHERE id = :id
        """,
    )
    suspend fun archiveMedicine(id: Long, archivedAt: Instant)

    /**
     * Elimina definitivamente una voce gia confermata dall'utente.
     *
     * @param entity entity da rimuovere fisicamente.
     */
    @Delete
    suspend fun deleteMedicine(entity: MedicineEntity)

    /**
     * Elimina definitivamente una voce tramite id dopo conferma esplicita.
     *
     * @param id identificativo della voce da rimuovere fisicamente.
     */
    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Long)
}
