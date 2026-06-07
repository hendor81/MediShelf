package it.hendorsoftware.medishelf.domain.repository

import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Contratto domain per le preferenze utente locali.
 *
 * Il dominio usa questo repository senza conoscere il meccanismo persistente
 * concreto, cosi le impostazioni possono essere osservate da use case e ViewModel.
 */
interface UserSettingsRepository {

    /**
     * Osserva le preferenze locali correnti.
     *
     * @return stream aggiornato delle impostazioni utente.
     */
    fun observeSettings(): Flow<UserSettings>

    /**
     * Aggiorna la modalita tema persistita.
     *
     * @param themeMode nuova modalita tema.
     */
    suspend fun updateThemeMode(themeMode: SettingsThemeMode)

    /**
     * Aggiorna la soglia di scadenza imminente.
     *
     * @param days giorni entro cui una voce e in scadenza.
     */
    suspend fun updateExpiringThresholdDays(days: Int)

    /**
     * Aggiorna l'abilitazione delle notifiche locali.
     *
     * @param enabled true quando le notifiche sono abilitate.
     */
    suspend fun updateNotificationsEnabled(enabled: Boolean)

    /**
     * Aggiorna la preferenza tra archiviazione e cancellazione.
     *
     * @param preferArchiveOverDelete true quando l'archiviazione resta l'azione preferita.
     */
    suspend fun updatePreferArchiveOverDelete(preferArchiveOverDelete: Boolean)
}
