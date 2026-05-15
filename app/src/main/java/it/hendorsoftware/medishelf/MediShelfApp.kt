package it.hendorsoftware.medishelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Root composable dell'app MediShelf.
 *
 * Contiene il telaio Compose minimo richiesto dalla issue di setup. Le azioni
 * sono intenzionalmente non collegate a feature reali, che saranno introdotte
 * nelle issue successive.
 */
@Composable
fun MediShelfApp() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        SetupPlaceholder(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Placeholder iniziale della versione Free.
 *
 * Mostra solo contenuti statici da risorse, cosi' lo scaffold resta compilabile
 * senza introdurre inventario, navigazione completa o persistenza.
 *
 * @param modifier modificatore Compose applicato dal chiamante.
 */
@Composable
private fun SetupPlaceholder(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        ) {
            Text(
                text = stringResource(R.string.home_setup_secondary_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.home_setup_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.home_setup_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = {},
                enabled = false,
            ) {
                Text(text = stringResource(R.string.home_setup_primary_action))
            }
        }
    }
}

/**
 * Preview dello scaffold iniziale MediShelf.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfAppPreview() {
    MediShelfTheme {
        MediShelfApp()
    }
}
