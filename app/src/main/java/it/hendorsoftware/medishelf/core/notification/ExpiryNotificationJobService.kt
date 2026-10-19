package it.hendorsoftware.medishelf.core.notification

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import it.hendorsoftware.medishelf.core.time.DateProvider
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import it.hendorsoftware.medishelf.domain.rules.ExpiryNotificationSelector
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Rilegge dati e preferenze a ogni esecuzione, anche dopo riavvio del dispositivo. */
@AndroidEntryPoint
class ExpiryNotificationJobService : JobService() {
    @Inject lateinit var medicines: MedicineRepository
    @Inject lateinit var settings: UserSettingsRepository
    @Inject lateinit var selector: ExpiryNotificationSelector
    @Inject lateinit var scheduler: ExpiryNotificationScheduler
    @Inject lateinit var dateProvider: DateProvider
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var execution: Job? = null

    /** Avvia la lettura asincrona; nessun dato del medicinale viene salvato nel job. */
    override fun onStartJob(params: JobParameters): Boolean {
        execution = scope.launch {
            var retry = false
            try {
                val candidates = medicines.observeActiveMedicines().first()
                // Leggere le preferenze per ultime riduce il rischio di notificare dopo una disattivazione.
                val currentSettings = settings.observeSettings().first()
                val selected = selector.select(candidates, currentSettings)
                val today = dateProvider.today()
                scheduler.createChannel()
                scheduler.show(selected, selected.count { it.expirationDate?.isBefore(today) == true })
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e("ExpiryNotifications", "Controllo scadenze non riuscito", exception)
                retry = true
            }
            jobFinished(params, retry)
        }
        return true
    }

    /** Interrompe il lavoro quando Android revoca la finestra di esecuzione. */
    override fun onStopJob(params: JobParameters): Boolean {
        execution?.cancel()
        return true
    }

    /** Libera tutte le coroutine quando il servizio viene distrutto. */
    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
