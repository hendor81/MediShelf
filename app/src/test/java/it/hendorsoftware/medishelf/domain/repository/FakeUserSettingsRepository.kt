package it.hendorsoftware.medishelf.domain.repository

import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Repository fake delle impostazioni per test unitari.
 *
 * @param initialSettings impostazioni iniziali pubblicate dallo stream.
 */
class FakeUserSettingsRepository(
    initialSettings: UserSettings = UserSettings(expiringThresholdDays = TEST_EXPIRING_THRESHOLD_DAYS),
) : UserSettingsRepository {

    private val settings = MutableStateFlow(initialSettings)

    val updatedThresholds = mutableListOf<Int>()
    val updatedNotificationValues = mutableListOf<Boolean>()

    /**
     * Osserva le impostazioni correnti.
     *
     * @return stream in memoria delle impostazioni.
     */
    override fun observeSettings(): Flow<UserSettings> = settings

    /**
     * Aggiorna il tema nel fake.
     *
     * @param themeMode nuova modalita tema.
     */
    override suspend fun updateThemeMode(themeMode: SettingsThemeMode) {
        settings.value = settings.value.copy(themeMode = themeMode)
    }

    /**
     * Aggiorna la soglia nel fake.
     *
     * @param days nuova soglia in giorni.
     */
    override suspend fun updateExpiringThresholdDays(days: Int) {
        updatedThresholds += days
        settings.value = settings.value.copy(expiringThresholdDays = days)
    }

    /**
     * Aggiorna le notifiche nel fake.
     *
     * @param enabled true quando abilitate.
     */
    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        updatedNotificationValues += enabled
        settings.value = settings.value.copy(notificationsEnabled = enabled)
    }

    /**
     * Aggiorna la preferenza archivia/elimina nel fake.
     *
     * @param preferArchiveOverDelete true quando archiviare e preferito.
     */
    override suspend fun updatePreferArchiveOverDelete(preferArchiveOverDelete: Boolean) {
        settings.value = settings.value.copy(preferArchiveOverDelete = preferArchiveOverDelete)
    }

    /**
     * Sostituisce le impostazioni correnti.
     *
     * @param value nuova fotografia.
     */
    fun setSettings(value: UserSettings) {
        settings.value = value
    }

    /**
     * Restituisce una fotografia corrente.
     *
     * @return impostazioni in memoria.
     */
    fun currentSettings(): UserSettings = settings.value

    private companion object {
        private const val TEST_EXPIRING_THRESHOLD_DAYS = 30
    }
}
