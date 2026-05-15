package it.hendorsoftware.medishelf

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto di ingresso applicativo di MediShelf.
 *
 * Questa classe abilita Hilt come contenitore di dependency injection
 * dell'applicazione, mantenendo la configurazione iniziale priva di dipendenze
 * concrete non ancora introdotte dalle issue successive.
 */
@HiltAndroidApp
class MediShelfApplication : Application()
