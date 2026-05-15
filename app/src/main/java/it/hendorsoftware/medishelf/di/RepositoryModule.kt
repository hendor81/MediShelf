package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt destinato ai binding dei repository.
 *
 * I binding concreti saranno aggiunti insieme alle interfacce di dominio e alle
 * implementazioni locali del layer data.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule
