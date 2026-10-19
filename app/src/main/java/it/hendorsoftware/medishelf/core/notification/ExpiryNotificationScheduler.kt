package it.hendorsoftware.medishelf.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import it.hendorsoftware.medishelf.MainActivity
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import it.hendorsoftware.medishelf.domain.rules.ExpiryNotificationSelector
import it.hendorsoftware.medishelf.core.time.DateProvider
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Coordina il job giornaliero persistente e una sola notifica riepilogativa aggiornata. */
@Singleton
class ExpiryNotificationScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settingsRepository: UserSettingsRepository,
    private val medicineRepository: MedicineRepository,
    private val selector: ExpiryNotificationSelector,
    private val dateProvider: DateProvider,
) {
    private val jobs = context.getSystemService(JobScheduler::class.java)
    private val notifications = context.getSystemService(NotificationManager::class.java)

    /** Osserva le preferenze: disabilitare annulla job e riepilogo già mostrato. */
    suspend fun observeSettings() {
        createChannel()
        combine(settingsRepository.observeSettings(), medicineRepository.observeActiveMedicines()) { settings, medicines ->
            settings to medicines
        }.collect { (settings, medicines) ->
            if (settings.notificationsEnabled) {
                if (jobs.getPendingJob(JOB_ID) == null) {
                    Log.i(TAG, "Programmazione controllo scadenze job=$JOB_ID")
                    val result = jobs.schedule(
                        JobInfo.Builder(JOB_ID, ComponentName(context, ExpiryNotificationJobService::class.java))
                            .setPeriodic(TimeUnit.DAYS.toMillis(1))
                            .setPersisted(true)
                            .build(),
                    )
                    if (result == JobScheduler.RESULT_FAILURE) {
                        Log.w(TAG, "Programmazione notifiche non riuscita job=$JOB_ID")
                    } else {
                        Log.i(TAG, "Controllo scadenze programmato job=$JOB_ID")
                    }
                }
                // Aggiorna o rimuove un riepilogo visibile dopo modifica, archiviazione o cambio soglia.
                if (notifications.activeNotifications.any { it.id == NOTIFICATION_ID }) {
                    val selected = selector.select(medicines, settings)
                    val today = dateProvider.today()
                    show(selected, selected.count { it.expirationDate?.isBefore(today) == true })
                }
            } else {
                if (jobs.getPendingJob(JOB_ID) != null) {
                    Log.i(TAG, "Disattivazione controllo scadenze job=$JOB_ID")
                }
                jobs.cancel(JOB_ID)
                notifications.cancel(NOTIFICATION_ID)
            }
        }
    }

    /** Crea il canale Android senza alterare le preferenze configurate nel sistema. */
    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifications.createNotificationChannel(NotificationChannel(
                CHANNEL_ID, context.getString(R.string.expiry_notification_channel), NotificationManager.IMPORTANCE_DEFAULT,
            ).apply { description = context.getString(R.string.expiry_notification_channel_description) })
        }
    }

    /** Mostra i conteggi correnti senza esporre nomi di medicinali nella schermata di blocco. */
    fun show(medicines: List<Medicine>, expiredCount: Int) {
        if (medicines.isEmpty()) {
            notifications.cancel(NOTIFICATION_ID)
            return
        }
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return
        val content = context.getString(R.string.expiry_notification_summary, medicines.size - expiredCount, expiredCount)
        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_expiry)
            .setContentTitle(context.getString(R.string.expiry_notification_title))
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .build()
        // Il permesso può essere revocato tra controllo e pubblicazione.
        try {
            notifications.notify(NOTIFICATION_ID, notification)
        } catch (exception: SecurityException) {
            Log.w(TAG, "Permesso notifiche non disponibile", exception)
        }
    }

    companion object {
        /** Identificativo stabile del controllo locale, utilizzabile anche nella verifica ADB. */
        const val JOB_ID = 27
        private const val NOTIFICATION_ID = 27
        private const val CHANNEL_ID = "medicine_expiry"
        private const val TAG = "ExpiryNotifications"
    }
}
