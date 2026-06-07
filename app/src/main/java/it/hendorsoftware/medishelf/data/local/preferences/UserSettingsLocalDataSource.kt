package it.hendorsoftware.medishelf.data.local.preferences

import it.hendorsoftware.medishelf.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Datasource locale per leggere e scrivere le impostazioni persistite.
 */
interface UserSettingsLocalDataSource {

    /**
     * Osserva le impostazioni locali.
     *
     * @return stream aggiornato a ogni modifica persistita.
     */
    fun observeSettings(): Flow<UserSettings>

    /**
     * Legge una fotografia immediata delle impostazioni correnti.
     *
     * @return impostazioni salvate o valori di default.
     */
    suspend fun readSettings(): UserSettings

    /**
     * Salva atomically la fotografia completa delle impostazioni.
     *
     * @param settings impostazioni da persistere.
     */
    suspend fun saveSettings(settings: UserSettings)
}
