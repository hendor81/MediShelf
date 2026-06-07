package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.hendorsoftware.medishelf.data.local.preferences.SharedPreferencesUserSettingsLocalDataSource
import it.hendorsoftware.medishelf.data.local.preferences.UserSettingsLocalDataSource
import it.hendorsoftware.medishelf.data.repository.LocalMedicineRepository
import it.hendorsoftware.medishelf.data.repository.LocalUserSettingsRepository
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
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

    /**
     * Collega il datasource preferenze all'implementazione SharedPreferences.
     *
     * @param dataSource datasource concreto basato su SharedPreferences.
     * @return contratto locale usato dal repository preferenze.
     */
    @Provides
    @Singleton
    fun provideUserSettingsLocalDataSource(
        dataSource: SharedPreferencesUserSettingsLocalDataSource,
    ): UserSettingsLocalDataSource = dataSource

    /**
     * Collega il contratto impostazioni all'implementazione locale.
     *
     * @param localUserSettingsRepository repository concreto basato sul datasource locale.
     * @return contratto repository usato da use case e ViewModel.
     */
    @Provides
    @Singleton
    fun provideUserSettingsRepository(
        localUserSettingsRepository: LocalUserSettingsRepository,
    ): UserSettingsRepository = localUserSettingsRepository
}
