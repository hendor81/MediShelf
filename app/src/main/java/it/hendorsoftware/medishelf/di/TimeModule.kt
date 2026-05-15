package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt dedicato ai provider di data e ora.
 *
 * La issue corrente configura solo il punto di estensione: i provider concreti
 * verranno aggiunti quando saranno disponibili le astrazioni in `core.time`.
 */
@Module
@InstallIn(SingletonComponent::class)
object TimeModule
