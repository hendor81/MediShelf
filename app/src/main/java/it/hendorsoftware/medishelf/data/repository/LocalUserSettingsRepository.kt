package it.hendorsoftware.medishelf.data.repository

import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.data.local.preferences.UserSettingsLocalDataSource
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.model.UserSettings
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Repository locale delle preferenze utente.
 *
 * @param dataSource sorgente persistente locale usata per leggere e salvare le impostazioni.
 */
@Singleton
class LocalUserSettingsRepository @Inject constructor(
    private val dataSource: UserSettingsLocalDataSource,
) : UserSettingsRepository {

    /**
     * Osserva le impostazioni locali.
     *
     * @return stream delle preferenze utente.
     */
    override fun observeSettings(): Flow<UserSettings> = dataSource.observeSettings()

    /**
     * Aggiorna la modalita tema mantenendo invariati gli altri campi.
     *
     * @param themeMode nuova modalita tema.
     */
    override suspend fun updateThemeMode(themeMode: SettingsThemeMode) {
        updateSettings { settings -> settings.copy(themeMode = themeMode) }
    }

    /**
     * Aggiorna la soglia di scadenza validandone i limiti Free.
     *
     * @param days nuovo valore di soglia.
     *
     * @throws IllegalArgumentException se [days] esce dai limiti consentiti.
     */
    override suspend fun updateExpiringThresholdDays(days: Int) {
        require(days in MediShelfDefaults.MinExpiringThresholdDays..MediShelfDefaults.MaxExpiringThresholdDays) {
            "Expiring threshold days must be between ${MediShelfDefaults.MinExpiringThresholdDays} and ${MediShelfDefaults.MaxExpiringThresholdDays}."
        }
        updateSettings { settings -> settings.copy(expiringThresholdDays = days) }
    }

    /**
     * Aggiorna lo stato delle notifiche locali.
     *
     * @param enabled true quando le notifiche devono restare abilitate.
     */
    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        updateSettings { settings -> settings.copy(notificationsEnabled = enabled) }
    }

    /**
     * Aggiorna la preferenza di rimozione.
     *
     * @param preferArchiveOverDelete true per privilegiare l'archiviazione.
     */
    override suspend fun updatePreferArchiveOverDelete(preferArchiveOverDelete: Boolean) {
        updateSettings { settings ->
            settings.copy(preferArchiveOverDelete = preferArchiveOverDelete)
        }
    }

    private suspend fun updateSettings(transform: (UserSettings) -> UserSettings) {
        val currentSettings = dataSource.readSettings()
        dataSource.saveSettings(transform(currentSettings))
    }
}
