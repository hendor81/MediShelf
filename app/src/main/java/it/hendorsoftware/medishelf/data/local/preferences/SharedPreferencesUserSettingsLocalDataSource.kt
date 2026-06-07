package it.hendorsoftware.medishelf.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.model.UserSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Datasource SharedPreferences per le impostazioni Free.
 *
 * @param context contesto applicativo usato per aprire il file preferenze.
 *
 * SharedPreferences e sufficiente per poche opzioni locali e mantiene la feature
 * offline-first senza aggiungere dipendenze persistenti piu pesanti.
 */
@Singleton
class SharedPreferencesUserSettingsLocalDataSource @Inject constructor(
    @ApplicationContext context: Context,
) : UserSettingsLocalDataSource {

    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    /**
     * Osserva le impostazioni e pubblica subito il valore corrente.
     *
     * @return stream distinto delle impostazioni locali.
     */
    override fun observeSettings(): Flow<UserSettings> = callbackFlow {
        trySend(readSettingsSnapshot())

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(readSettingsSnapshot())
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }.distinctUntilChanged()

    /**
     * Legge le impostazioni correnti dal file preferenze.
     *
     * @return impostazioni salvate o default quando una chiave manca.
     */
    override suspend fun readSettings(): UserSettings = readSettingsSnapshot()

    /**
     * Salva tutte le impostazioni in una singola edit.
     *
     * @param settings fotografia completa da persistere.
     */
    override suspend fun saveSettings(settings: UserSettings) {
        preferences.edit()
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putInt(KEY_EXPIRING_THRESHOLD_DAYS, settings.expiringThresholdDays)
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, settings.notificationsEnabled)
            .putBoolean(KEY_PREFER_ARCHIVE_OVER_DELETE, settings.preferArchiveOverDelete)
            .apply()
    }

    private fun readSettingsSnapshot(): UserSettings {
        val storedThreshold = preferences.getInt(
            KEY_EXPIRING_THRESHOLD_DAYS,
            MediShelfDefaults.ExpiringThresholdDays,
        )
        val safeThreshold = storedThreshold.coerceIn(
            MediShelfDefaults.MinExpiringThresholdDays,
            MediShelfDefaults.MaxExpiringThresholdDays,
        )

        // Un valore enum non riconosciuto puo arrivare solo da preferenze vecchie o corrotte.
        val themeMode = preferences.getString(KEY_THEME_MODE, null)
            ?.let { value -> runCatching { SettingsThemeMode.valueOf(value) }.getOrNull() }
            ?: SettingsThemeMode.LIGHT

        return UserSettings(
            themeMode = themeMode,
            expiringThresholdDays = safeThreshold,
            notificationsEnabled = preferences.getBoolean(
                KEY_NOTIFICATIONS_ENABLED,
                MediShelfDefaults.NotificationsEnabled,
            ),
            preferArchiveOverDelete = preferences.getBoolean(
                KEY_PREFER_ARCHIVE_OVER_DELETE,
                MediShelfDefaults.PreferArchiveOverDelete,
            ),
        )
    }

    private companion object {
        private const val PREFERENCES_NAME = "medishelf_user_settings"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_EXPIRING_THRESHOLD_DAYS = "expiring_threshold_days"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_PREFER_ARCHIVE_OVER_DELETE = "prefer_archive_over_delete"
    }
}
