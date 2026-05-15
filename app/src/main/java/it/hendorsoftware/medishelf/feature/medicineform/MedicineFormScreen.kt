package it.hendorsoftware.medishelf.feature.medicineform

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
 * Placeholder del form di inserimento o modifica medicinale.
 *
 * @param medicineId identificativo del medicinale in modifica, nullo in inserimento.
 * @param onDoneClick callback usata dal placeholder per simulare il completamento.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun MedicineFormScreen(
    medicineId: String?,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditMode = medicineId != null
    val screenTitle = if (isEditMode) {
        stringResource(R.string.medicine_edit_screen_title)
    } else {
        stringResource(R.string.medicine_add_screen_title)
    }
    val placeholderTitle = if (isEditMode) {
        stringResource(R.string.medicine_edit_placeholder_title)
    } else {
        stringResource(R.string.medicine_add_placeholder_title)
    }
    val placeholderBody = if (isEditMode) {
        stringResource(R.string.medicine_edit_placeholder_body, medicineId.orEmpty())
    } else {
        stringResource(R.string.medicine_add_placeholder_body)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = screenTitle)
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = placeholderTitle,
            body = placeholderBody,
            modifier = Modifier.padding(innerPadding),
        ) {
            Button(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.navigation_action_complete_placeholder))
            }
        }
    }
}

/**
 * Preview del form in modalita' inserimento.
 */
@Preview(showBackground = true)
@Composable
private fun AddMedicineFormScreenPreview() {
    MediShelfTheme {
        MedicineFormScreen(
            medicineId = null,
            onDoneClick = {},
        )
    }
}

/**
 * Preview del form in modalita' modifica.
 */
@Preview(showBackground = true)
@Composable
private fun EditMedicineFormScreenPreview() {
    MediShelfTheme {
        MedicineFormScreen(
            medicineId = "sample-medicine-id",
            onDoneClick = {},
        )
    }
}
