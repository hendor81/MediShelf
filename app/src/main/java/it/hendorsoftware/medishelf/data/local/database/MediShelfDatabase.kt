package it.hendorsoftware.medishelf.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import it.hendorsoftware.medishelf.data.local.dao.MedicineDao
import it.hendorsoftware.medishelf.data.local.entity.MedicineEntity

/**
 * Database Room principale della versione Free offline-first di MediShelf.
 *
 * La versione iniziale contiene solo la tabella dei medicinali; le migrazioni
 * evolutive saranno introdotte quando lo schema cambiera dopo la v1.
 */
@Database(
    entities = [MedicineEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(DateTimeConverters::class)
abstract class MediShelfDatabase : RoomDatabase() {

    /**
     * Espone il DAO per le operazioni persistenti sui medicinali.
     *
     * @return DAO dei medicinali.
     */
    abstract fun medicineDao(): MedicineDao

    companion object {
        /**
         * Nome stabile del file database locale.
         */
        const val DATABASE_NAME = "medishelf.db"
    }
}
