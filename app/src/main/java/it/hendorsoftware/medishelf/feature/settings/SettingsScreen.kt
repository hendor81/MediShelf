package it.hendorsoftware.medishelf.feature.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfPlaceholderScreen
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Placeholder della schermata Impostazioni.
 *
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.settings_screen_title))
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.settings_placeholder_title),
            body = stringResource(R.string.settings_placeholder_body),
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/**
 * Preview delle Impostazioni placeholder.
 */
@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MediShelfTheme {
        SettingsScreen()
    }
}
