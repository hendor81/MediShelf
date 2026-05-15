package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt riservato a database Room e DAO.
 *
 * Resta intenzionalmente vuoto finche' lo schema Room non viene introdotto, per
 * evitare provider fittizi o dipendenze premature.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
