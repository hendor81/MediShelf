package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt dedicato ai use case applicativi.
 *
 * In questa fase dichiara solo il punto di raccolta dei provider, senza creare
 * use case reali fuori dalla loro issue di implementazione.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule
