package it.hendorsoftware.medishelf.data.repository

import it.hendorsoftware.medishelf.data.local.preferences.UserSettingsLocalDataSource
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

/**
 * Test unitari per il repository locale delle impostazioni.
 */
class LocalUserSettingsRepositoryTest {

    /**
     * Verifica che lo stream del datasource venga esposto senza trasformazioni inutili.
     */
    @Test
    fun shouldObserveSettingsFromDataSource() = runTest {
        val initialSettings = UserSettings(expiringThresholdDays = 45)
        val repository = LocalUserSettingsRepository(
            dataSource = FakeUserSettingsLocalDataSource(initialSettings),
        )

        assertEquals(initialSettings, repository.observeSettings().first())
    }

    /**
     * Verifica che l'aggiornamento della soglia preservi le altre preferenze.
     */
    @Test
    fun shouldUpdateThresholdPreservingOtherSettings() = runTest {
        val dataSource = FakeUserSettingsLocalDataSource(
            UserSettings(
                themeMode = SettingsThemeMode.DARK,
                expiringThresholdDays = 30,
                notificationsEnabled = false,
                preferArchiveOverDelete = false,
            ),
        )
        val repository = LocalUserSettingsRepository(dataSource)

        repository.updateExpiringThresholdDays(60)

        assertEquals(
            UserSettings(
                themeMode = SettingsThemeMode.DARK,
                expiringThresholdDays = 60,
                notificationsEnabled = false,
                preferArchiveOverDelete = false,
            ),
            dataSource.currentSettings(),
        )
    }

    /**
     * Verifica che soglie fuori range non vengano persistite.
     */
    @Test
    fun shouldRejectOutOfRangeThreshold() = runTest {
        val dataSource = FakeUserSettingsLocalDataSource(UserSettings(expiringThresholdDays = 30))
        val repository = LocalUserSettingsRepository(dataSource)

        try {
            repository.updateExpiringThresholdDays(366)
            fail("La soglia fuori range deve essere rifiutata.")
        } catch (exception: IllegalArgumentException) {
            assertEquals(
                "Expiring threshold days must be between 0 and 365.",
                exception.message,
            )
        }

        assertEquals(30, dataSource.currentSettings().expiringThresholdDays)
    }

    private class FakeUserSettingsLocalDataSource(
        initialSettings: UserSettings,
    ) : UserSettingsLocalDataSource {

        private val settings = MutableStateFlow(initialSettings)

        override fun observeSettings(): Flow<UserSettings> = settings

        override suspend fun readSettings(): UserSettings = settings.value

        override suspend fun saveSettings(settings: UserSettings) {
            this.settings.value = settings
        }

        fun currentSettings(): UserSettings = settings.value
    }
}
