package it.hendorsoftware.medishelf.feature.inventory

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
 * Placeholder della schermata Inventario.
 *
 * @param onAddMedicineClick callback per aprire il form di inserimento.
 * @param onMedicineClick callback per aprire un dettaglio medicinale.
 * @param onArchiveClick callback per aprire l'archivio.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun InventoryScreen(
    onAddMedicineClick: () -> Unit,
    onMedicineClick: () -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.inventory_screen_title))
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.inventory_placeholder_title),
            body = stringResource(R.string.inventory_placeholder_body),
            modifier = Modifier.padding(innerPadding),
        ) {
            Button(
                onClick = onAddMedicineClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_add_medicine))
            }
            OutlinedButton(
                onClick = onMedicineClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_open_detail))
            }
            OutlinedButton(
                onClick = onArchiveClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_open_archive))
            }
        }
    }
}

/**
 * Preview dell'Inventario placeholder.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryScreenPreview() {
    MediShelfTheme {
        InventoryScreen(
            onAddMedicineClick = {},
            onMedicineClick = {},
            onArchiveClick = {},
        )
    }
}
