package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.UserSettings
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Osserva le preferenze locali della versione Free.
 *
 * @param userSettingsRepository repository domain delle impostazioni.
 */
class ObserveUserSettingsUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) {

    /**
     * Restituisce lo stream delle impostazioni utente.
     *
     * @return flow aggiornato delle preferenze locali.
     */
    operator fun invoke(): Flow<UserSettings> = userSettingsRepository.observeSettings()
}
