package it.hendorsoftware.medishelf.feature.archive

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
 * Placeholder della schermata Archivio.
 *
 * @param onMedicineClick callback per aprire un dettaglio medicinale archiviato.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun ArchiveScreen(
    onMedicineClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.archive_screen_title))
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.archive_placeholder_title),
            body = stringResource(R.string.archive_placeholder_body),
            modifier = Modifier.padding(innerPadding),
        ) {
            OutlinedButton(
                onClick = onMedicineClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_open_detail))
            }
        }
    }
}

/**
 * Preview dell'Archivio placeholder.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveScreenPreview() {
    MediShelfTheme {
        ArchiveScreen(onMedicineClick = {})
    }
}
