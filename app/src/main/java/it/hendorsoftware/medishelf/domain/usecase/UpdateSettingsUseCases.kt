package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import javax.inject.Inject

/**
 * Aggiorna la preferenza tema dell'app.
 */
class UpdateThemeModeUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) {
    /**
     * Persiste la modalita tema selezionata.
     *
     * @param themeMode nuova modalita tema.
     */
    suspend operator fun invoke(themeMode: SettingsThemeMode) {
        userSettingsRepository.updateThemeMode(themeMode)
    }
}

/**
 * Aggiorna la soglia di scadenza imminente.
 */
class UpdateExpiringThresholdDaysUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) {
    /**
     * Persiste il numero di giorni entro cui una scadenza e vicina.
     *
     * @param days nuova soglia in giorni.
     */
    suspend operator fun invoke(days: Int) {
        userSettingsRepository.updateExpiringThresholdDays(days)
    }
}

/**
 * Aggiorna l'abilitazione delle notifiche locali.
 */
class UpdateNotificationsEnabledUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) {
    /**
     * Persiste lo stato delle notifiche locali.
     *
     * @param enabled true quando le notifiche sono abilitate.
     */
    suspend operator fun invoke(enabled: Boolean) {
        userSettingsRepository.updateNotificationsEnabled(enabled)
    }
}

/**
 * Aggiorna la preferenza tra archiviazione e cancellazione.
 */
class UpdatePreferArchiveOverDeleteUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) {
    /**
     * Persiste la preferenza di rimozione.
     *
     * @param preferArchiveOverDelete true quando l'archiviazione e preferita.
     */
    suspend operator fun invoke(preferArchiveOverDelete: Boolean) {
        userSettingsRepository.updatePreferArchiveOverDelete(preferArchiveOverDelete)
    }
}
