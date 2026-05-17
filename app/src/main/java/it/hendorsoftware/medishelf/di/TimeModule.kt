package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.hendorsoftware.medishelf.core.time.DateProvider
import it.hendorsoftware.medishelf.core.time.SystemDateProvider
import javax.inject.Singleton

/**
 * Modulo Hilt dedicato ai provider di data e ora.
 */
@Module
@InstallIn(SingletonComponent::class)
object TimeModule {

    /**
     * Espone il provider di data di produzione dietro l'interfaccia testabile.
     *
     * @param systemDateProvider implementazione basata sull'orologio del dispositivo.
     * @return provider di data usato dalle regole di dominio.
     */
    @Provides
    @Singleton
    fun provideDateProvider(systemDateProvider: SystemDateProvider): DateProvider = systemDateProvider
}
