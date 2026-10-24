package it.hendorsoftware.medishelf

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.feature.onboarding.OnboardingScreen

/** Attende le preferenze prima di creare il grafo, evitando flash di Home e ritorni all'introduzione. */
@Composable
fun MainContent(
    uiState: MainUiState,
    onCompleteOnboarding: () -> Unit,
    onSkipOnboarding: () -> Unit = onCompleteOnboarding,
    onTutorialNext: () -> Unit = {},
    onTutorialDismiss: () -> Unit = {},
) {
    when {
        uiState.isLoading -> Surface(modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        !uiState.hasSeenOnboarding -> OnboardingScreen(uiState, onCompleteOnboarding, onSkipOnboarding)
        else -> MediShelfApp(uiState.tutorialStep, onTutorialNext, onTutorialDismiss)
    }
}

/** Preview dello stato di attesa iniziale. */
@Preview(showBackground = true)
@Composable
private fun MainContentLoadingPreview() {
    MediShelfTheme { MainContent(MainUiState(), {}) }
}
