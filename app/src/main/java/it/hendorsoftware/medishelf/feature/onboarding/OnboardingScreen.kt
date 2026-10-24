package it.hendorsoftware.medishelf.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.MainUiState
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/** Introduzione SCH-08 in una sola pagina; Avanti avvia la guida Home, Salta e BACK la escludono. */
@Composable
fun OnboardingScreen(uiState: MainUiState, onComplete: () -> Unit, onSkip: () -> Unit = onComplete) {
    BackHandler { if (!uiState.isSaving) onSkip() }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.safeDrawingPadding().verticalScroll(rememberScrollState())
                .padding(MediShelfDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingLarge),
        ) {
            Text(stringResource(R.string.onboarding_title), style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.onboarding_inventory), style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.onboarding_expiry), style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.onboarding_notifications), style = MaterialTheme.typography.bodyLarge)
            Text(stringResource(R.string.onboarding_scope), style = MaterialTheme.typography.bodyMedium)
            if (uiState.hasSaveError) {
                Text(stringResource(R.string.onboarding_save_error), color = MaterialTheme.colorScheme.error)
            }
            Button(onClick = onComplete, enabled = !uiState.isSaving, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.onboarding_start))
            }
            TextButton(onClick = onSkip, enabled = !uiState.isSaving, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.onboarding_skip))
            }
        }
    }
}

/** Preview indipendente da Hilt e preferenze. */
@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    MediShelfTheme { OnboardingScreen(MainUiState(isLoading = false), {}) }
}

/** Preview dell'errore recuperabile nel tema scuro. */
@Preview(showBackground = true)
@Composable
private fun OnboardingErrorPreview() {
    MediShelfTheme(darkTheme = true) {
        OnboardingScreen(MainUiState(isLoading = false, hasSaveError = true), {})
    }
}
