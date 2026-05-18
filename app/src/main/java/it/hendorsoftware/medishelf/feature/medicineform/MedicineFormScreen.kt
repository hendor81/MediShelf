package it.hendorsoftware.medishelf.feature.medicineform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfPlaceholderScreen
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Route del form di inserimento medicinale.
 *
 * @param onSaved callback invocata dopo il salvataggio riuscito, usata dalla
 * navigazione per tornare al flusso previsto.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun MedicineFormRoute(
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MedicineFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveSuccessMessage = stringResource(R.string.medicine_form_save_success)

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar(
                message = saveSuccessMessage,
                duration = SnackbarDuration.Short,
            )
            onSaved()
        }
    }

    MedicineFormScreen(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onPackageFormChanged = viewModel::onPackageFormChanged,
        onQuantityChanged = viewModel::onQuantityChanged,
        onQuantityUnitChanged = viewModel::onQuantityUnitChanged,
        onLowStockThresholdChanged = viewModel::onLowStockThresholdChanged,
        onExpirationDateChanged = viewModel::onExpirationDateChanged,
        onStorageLocationChanged = viewModel::onStorageLocationChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onSaveClick = viewModel::onSaveClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

/**
 * Schermata stateless per inserire una nuova voce dell'inventario.
 *
 * @param uiState stato completo del form.
 * @param onNameChanged callback per il campo nome obbligatorio.
 * @param onPackageFormChanged callback per formato o confezione.
 * @param onQuantityChanged callback per quantita opzionale.
 * @param onQuantityUnitChanged callback per unita della quantita.
 * @param onLowStockThresholdChanged callback per soglia scorta bassa.
 * @param onExpirationDateChanged callback per scadenza opzionale.
 * @param onStorageLocationChanged callback per luogo di conservazione.
 * @param onNotesChanged callback per note libere.
 * @param onSaveClick callback di salvataggio.
 * @param snackbarHostState host per mostrare feedback discreti della schermata.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun MedicineFormScreen(
    uiState: MedicineFormUiState,
    onNameChanged: (String) -> Unit,
    onPackageFormChanged: (String) -> Unit,
    onQuantityChanged: (String) -> Unit,
    onQuantityUnitChanged: (String) -> Unit,
    onLowStockThresholdChanged: (String) -> Unit,
    onExpirationDateChanged: (String) -> Unit,
    onStorageLocationChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.medicine_add_screen_title))
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(MediShelfDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        ) {
            Text(
                text = stringResource(R.string.medicine_form_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            MedicineFormTextField(
                value = uiState.name,
                onValueChange = onNameChanged,
                label = stringResource(R.string.medicine_form_name_label),
                error = uiState.nameError.toMessage(),
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.packageForm,
                onValueChange = onPackageFormChanged,
                label = stringResource(R.string.medicine_form_package_form_label),
                supportingText = stringResource(R.string.medicine_form_package_form_support),
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.quantity,
                onValueChange = onQuantityChanged,
                label = stringResource(R.string.medicine_form_quantity_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                error = uiState.quantityError.toMessage(),
                keyboardType = KeyboardType.Decimal,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.quantityUnit,
                onValueChange = onQuantityUnitChanged,
                label = stringResource(R.string.medicine_form_quantity_unit_label),
                supportingText = stringResource(R.string.medicine_form_quantity_unit_support),
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.lowStockThreshold,
                onValueChange = onLowStockThresholdChanged,
                label = stringResource(R.string.medicine_form_low_stock_threshold_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                error = uiState.lowStockThresholdError.toMessage(),
                keyboardType = KeyboardType.Decimal,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.expirationDate,
                onValueChange = onExpirationDateChanged,
                label = stringResource(R.string.medicine_form_expiration_date_label),
                supportingText = stringResource(R.string.medicine_form_expiration_date_support),
                error = uiState.expirationDateError.toMessage(),
                keyboardType = KeyboardType.Number,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.storageLocation,
                onValueChange = onStorageLocationChanged,
                label = stringResource(R.string.medicine_form_storage_location_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                label = stringResource(R.string.medicine_form_notes_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                minLines = MedicineFormDefaults.NotesMinLines,
            )

            if (uiState.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = onSaveClick,
                enabled = !uiState.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.medicine_form_save_action))
            }
        }
    }
}

/**
 * Placeholder temporaneo della modalita' modifica, non inclusa nella issue 14.
 *
 * @param medicineId identificativo del medicinale che sara' modificato in una
 * issue successiva.
 * @param onDoneClick callback per uscire dal placeholder.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun MedicineEditPlaceholderScreen(
    medicineId: String,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.medicine_edit_screen_title))
        },
    ) { innerPadding ->
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.medicine_edit_placeholder_title),
            body = stringResource(R.string.medicine_edit_placeholder_body, medicineId),
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
 * Text field standard del form con mapping centralizzato degli errori.
 */
@Composable
private fun MedicineFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = false,
    minLines: Int = MedicineFormDefaults.DefaultMinLines,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        supportingText = {
            val helperText = error ?: supportingText
            if (helperText != null) {
                Text(text = helperText)
            }
        },
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        minLines = minLines,
    )
}

@Composable
private fun MedicineFormFieldError?.toMessage(): String? = when (this) {
    MedicineFormFieldError.REQUIRED -> stringResource(R.string.medicine_form_name_required_error)
    MedicineFormFieldError.INVALID_NUMBER -> stringResource(R.string.medicine_form_invalid_number_error)
    MedicineFormFieldError.NEGATIVE_NUMBER -> stringResource(R.string.medicine_form_negative_number_error)
    MedicineFormFieldError.INVALID_DATE -> stringResource(R.string.medicine_form_invalid_date_error)
    null -> null
}

private object MedicineFormDefaults {
    const val DefaultMinLines = 1
    const val NotesMinLines = 3
}

/**
 * Preview del form di inserimento vuoto.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineFormScreenPreview() {
    MediShelfTheme {
        MedicineFormScreen(
            uiState = MedicineFormUiState(),
            onNameChanged = {},
            onPackageFormChanged = {},
            onQuantityChanged = {},
            onQuantityUnitChanged = {},
            onLowStockThresholdChanged = {},
            onExpirationDateChanged = {},
            onStorageLocationChanged = {},
            onNotesChanged = {},
            onSaveClick = {},
        )
    }
}

/**
 * Preview del form con dati gia compilati.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineFormScreenFilledPreview() {
    MediShelfTheme {
        MedicineFormScreen(
            uiState = MedicineFormUiState(
                name = "Paracetamolo",
                packageForm = "Compresse",
                quantity = "12",
                quantityUnit = "compresse",
                expirationDate = "2026-12-31",
                storageLocation = "Bagno",
            ),
            onNameChanged = {},
            onPackageFormChanged = {},
            onQuantityChanged = {},
            onQuantityUnitChanged = {},
            onLowStockThresholdChanged = {},
            onExpirationDateChanged = {},
            onStorageLocationChanged = {},
            onNotesChanged = {},
            onSaveClick = {},
        )
    }
}

/**
 * Preview del placeholder di modifica mantenuto fuori dallo scope di inserimento.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineEditPlaceholderScreenPreview() {
    MediShelfTheme {
        MedicineEditPlaceholderScreen(
            medicineId = "sample-medicine-id",
            onDoneClick = {},
        )
    }
}
