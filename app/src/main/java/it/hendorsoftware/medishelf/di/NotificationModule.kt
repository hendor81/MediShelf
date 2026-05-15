package it.hendorsoftware.medishelf.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Modulo Hilt per scheduler e componenti delle notifiche locali.
 *
 * Le dipendenze concrete saranno registrate quando verra' introdotto il layer
 * `core.notification`, mantenendo separata la configurazione Android-specific.
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationModule
