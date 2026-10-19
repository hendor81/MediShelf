package it.hendorsoftware.medishelf

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import it.hendorsoftware.medishelf.core.notification.ExpiryNotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Inizializza Hilt e mantiene la programmazione locale coerente con le preferenze. */
@HiltAndroidApp
class MediShelfApplication : Application() {
    @Inject lateinit var notificationScheduler: ExpiryNotificationScheduler
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    /** Avvia l'osservazione per tutta la durata del processo applicativo. */
    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            try {
                notificationScheduler.observeSettings()
            } catch (exception: kotlinx.coroutines.CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("ExpiryNotifications", "Avvio scheduler non riuscito", exception)
            }
        }
    }
}
