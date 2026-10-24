package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import javax.inject.Inject

/** Registra localmente la chiusura o il salto dell'introduzione. */
class CompleteOnboardingUseCase @Inject constructor(
    private val repository: UserSettingsRepository,
) {
    /** Persiste la preferenza; propaga gli errori al chiamante per consentire un nuovo tentativo. */
    suspend operator fun invoke() = repository.completeOnboarding()
}
