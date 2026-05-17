package it.hendorsoftware.medishelf.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.hendorsoftware.medishelf.data.local.dao.MedicineDao
import it.hendorsoftware.medishelf.data.local.database.MediShelfDatabase
import javax.inject.Singleton

/**
 * Modulo Hilt riservato a database Room e DAO.
 *
 * Centralizza la creazione del database locale e dei DAO, evitando istanze
 * manuali nei ViewModel, nelle Composable o nei repository.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Crea l'istanza singleton del database Room dell'app.
     *
     * @param context contesto applicativo fornito da Hilt.
     * @return database locale di MediShelf.
     */
    @Provides
    @Singleton
    fun provideMediShelfDatabase(
        @ApplicationContext context: Context,
    ): MediShelfDatabase = Room.databaseBuilder(
        context,
        MediShelfDatabase::class.java,
        MediShelfDatabase.DATABASE_NAME,
    ).build()

    /**
     * Espone il DAO dei medicinali a repository e use case futuri.
     *
     * @param database database locale singleton.
     * @return DAO dei medicinali.
     */
    @Provides
    fun provideMedicineDao(database: MediShelfDatabase): MedicineDao = database.medicineDao()
}
