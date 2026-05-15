package it.hendorsoftware.medishelf.feature.medicinedetail

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
 * Placeholder della schermata Dettaglio medicinale.
 *
 * @param medicineId identificativo minimo ricevuto dalla route.
 * @param onEditClick callback per aprire la modifica.
 * @param onArchiveClick callback per aprire l'archivio come destinazione secondaria.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun MedicineDetailScreen(
    medicineId: String,
    onEditClick: () -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.medicine_detail_screen_title))
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.medicine_detail_placeholder_title),
            body = stringResource(R.string.medicine_detail_placeholder_body, medicineId),
            modifier = Modifier.padding(innerPadding),
        ) {
            Button(
                onClick = onEditClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_edit_medicine))
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
 * Preview del Dettaglio medicinale placeholder.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineDetailScreenPreview() {
    MediShelfTheme {
        MedicineDetailScreen(
            medicineId = "sample-medicine-id",
            onEditClick = {},
            onArchiveClick = {},
        )
    }
}
