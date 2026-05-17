package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.hendorsoftware.medishelf.data.repository.LocalMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Singleton

/**
 * Modulo Hilt destinato ai binding dei repository applicativi.
 *
 * Espone al domain solo i contratti repository, lasciando le implementazioni
 * concrete nel layer data.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Collega il contratto dei medicinali all'implementazione locale Room.
     *
     * @param localMedicineRepository repository concreto basato su DAO e mapper.
     * @return contratto repository usato da use case e ViewModel futuri.
     */
    @Provides
    @Singleton
    fun provideMedicineRepository(
        localMedicineRepository: LocalMedicineRepository,
    ): MedicineRepository = localMedicineRepository
}
