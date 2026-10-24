package it.hendorsoftware.medishelf

import it.hendorsoftware.medishelf.domain.model.UserSettings
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import it.hendorsoftware.medishelf.domain.usecase.CompleteOnboardingUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Verifica primo avvio, riapertura, caricamento ed errore recuperabile. */
class MainViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    /** La chiusura persiste e una nuova istanza salta l'introduzione. */
    @Test fun shouldShowOnlyUntilCompleted() = runTest {
        val repository = FakeUserSettingsRepository()
        val model = model(repository)
        assertFalse(model.uiState.value.isLoading)
        assertFalse(model.uiState.value.hasSeenOnboarding)
        model.onCompleteOnboarding()
        assertTrue(repository.currentSettings().hasSeenOnboarding)
        assertTrue(model(repository).uiState.value.hasSeenOnboarding)
    }

    /** Nessuna destinazione viene scelta prima della prima emissione persistente. */
    @Test fun shouldWaitForInitialPreferences() = runTest {
        val repository = object : UserSettingsRepository by FakeUserSettingsRepository() {
            override fun observeSettings() = kotlinx.coroutines.flow.emptyFlow<UserSettings>()
        }
        assertTrue(model(repository).uiState.value.isLoading)
    }

    /** Un errore conserva la schermata e un nuovo tentativo puÃ² completare. */
    @Test fun shouldAllowRetryAfterSaveFailure() = runTest {
        val values = MutableStateFlow(UserSettings())
        var fail = true
        val repository = object : UserSettingsRepository by FakeUserSettingsRepository() {
            override fun observeSettings() = values
            override suspend fun completeOnboarding() {
                if (fail) throw IllegalStateException("Test save failure")
                values.value = values.value.copy(hasSeenOnboarding = true)
            }
        }
        val model = model(repository)
        model.onCompleteOnboarding(startTutorial = true)
        assertNull(model.uiState.value.tutorialStep)
        assertTrue(model.uiState.value.hasSaveError)
        assertFalse(model.uiState.value.isSaving)
        assertFalse(model.uiState.value.hasSeenOnboarding)
        fail = false
        model.onCompleteOnboarding()
        assertTrue(model.uiState.value.hasSeenOnboarding)
        assertFalse(model.uiState.value.hasSaveError)
    }

    /** Avanti avvia cinque passi; alla fine resta la Home e la riapertura non ripete la guida. */
    @Test fun shouldGuideThroughFiveStepsOnlyAfterStart() = runTest {
        val repository = FakeUserSettingsRepository()
        val model = model(repository)
        model.onCompleteOnboarding(startTutorial = true)
        for (step in 0..4) {
            assertEquals(step, model.uiState.value.tutorialStep)
            model.onTutorialNext()
        }
        assertNull(model.uiState.value.tutorialStep)
        assertTrue(model.uiState.value.hasSeenOnboarding)
        assertNull(model(repository).uiState.value.tutorialStep)
    }

    /** Salta apre subito la Home; la guida avviata può essere interrotta in ogni passo. */
    @Test fun shouldSkipOrDismissWithoutRestartingTutorial() = runTest {
        val skipped = model(FakeUserSettingsRepository())
        skipped.onCompleteOnboarding()
        assertTrue(skipped.uiState.value.hasSeenOnboarding)
        assertNull(skipped.uiState.value.tutorialStep)
        for (step in 0..4) {
            val model = model(FakeUserSettingsRepository())
            model.onCompleteOnboarding(startTutorial = true)
            repeat(step) { model.onTutorialNext() }
            model.onTutorialDismiss()
            assertNull(model.uiState.value.tutorialStep)
            model.onCompleteOnboarding(startTutorial = true)
            assertNull(model.uiState.value.tutorialStep)
        }
    }

    private fun model(repository: UserSettingsRepository) = MainViewModel(
        ObserveUserSettingsUseCase(repository), CompleteOnboardingUseCase(repository),
    )
}
