package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/** Barra primaria condivisa per aprire l'inserimento di un medicinale. */
@Composable
fun AddMedicineActionBar(onAddMedicineClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = MediShelfDimens.ActionBarElevation,
    ) {
        Button(
            onClick = onAddMedicineClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MediShelfDimens.ScreenPadding),
            contentPadding = PaddingValues(MediShelfDimens.SpacingMedium),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
            )
            Text(
                text = stringResource(R.string.navigation_action_add_medicine),
                modifier = Modifier.padding(start = MediShelfDimens.SpacingSmall),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** Preview della barra primaria condivisa. */
@Preview(showBackground = true)
@Composable
private fun AddMedicineActionBarPreview() {
    MediShelfTheme { AddMedicineActionBar(onAddMedicineClick = {}) }
}
