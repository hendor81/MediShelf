package it.hendorsoftware.medishelf.feature.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfPlaceholderScreen
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Placeholder della Home / Dashboard.
 *
 * @param onAddMedicineClick callback per aprire il form di inserimento.
 * @param onInventoryClick callback per aprire l'inventario.
 * @param onExpiryClick callback per aprire lo scadenzario.
 * @param onSettingsClick callback per aprire le impostazioni.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun HomeScreen(
    onAddMedicineClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onExpiryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.home_screen_title))
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.home_placeholder_title),
            body = stringResource(R.string.home_placeholder_body),
            modifier = Modifier.padding(innerPadding),
        ) {
            Button(
                onClick = onAddMedicineClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_add_medicine))
            }
            OutlinedButton(
                onClick = onInventoryClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_open_inventory))
            }
            OutlinedButton(
                onClick = onExpiryClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_open_expiry))
            }
            OutlinedButton(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_open_settings))
            }
        }
    }
}

/**
 * Preview della Home placeholder.
 */
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MediShelfTheme {
        HomeScreen(
            onAddMedicineClick = {},
            onInventoryClick = {},
            onExpiryClick = {},
            onSettingsClick = {},
        )
    }
}
