package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt dedicato ai use case applicativi.
 *
 * I use case concreti usano constructor injection; questo modulo resta il
 * punto di raccolta per eventuali provider espliciti futuri.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule
