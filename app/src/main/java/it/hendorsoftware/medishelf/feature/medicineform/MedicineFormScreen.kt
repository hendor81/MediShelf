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
 * @param medicineId identificativo opzionale che abilita la modalita modifica.
 * @param onSaved callback invocata dopo il salvataggio riuscito, usata dalla
 * navigazione per tornare al flusso previsto.
 * @param onCloseClick callback usata dagli stati non recuperabili, per esempio
 * medicinale non trovato.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun MedicineFormRoute(
    medicineId: String? = null,
    onSaved: () -> Unit,
    onCloseClick: () -> Unit = onSaved,
    modifier: Modifier = Modifier,
    viewModel: MedicineFormViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val saveSuccessMessage = stringResource(
        if (uiState.mode == MedicineFormMode.Edit) {
            R.string.medicine_form_update_success
        } else {
            R.string.medicine_form_save_success
        },
    )

    LaunchedEffect(medicineId) {
        if (medicineId != null) {
            viewModel.loadMedicineForEdit(medicineId)
        }
    }

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
        onNotFoundDoneClick = onCloseClick,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

/**
 * Schermata stateless per inserire o modificare una voce dell'inventario.
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
 * @param onNotFoundDoneClick callback per chiudere lo stato medicinale non trovato.
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
    onNotFoundDoneClick: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
) {
    val isEditMode = uiState.mode == MedicineFormMode.Edit

    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(
                title = stringResource(
                    if (isEditMode) {
                        R.string.medicine_edit_screen_title
                    } else {
                        R.string.medicine_add_screen_title
                    },
                ),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        if (uiState.isNotFound) {
            MediShelfPlaceholderScreen(
                title = stringResource(R.string.medicine_edit_not_found_title),
                body = stringResource(R.string.medicine_edit_not_found_body),
                modifier = Modifier.padding(innerPadding),
            ) {
                Button(
                    onClick = onNotFoundDoneClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.navigation_action_go_back))
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(MediShelfDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        ) {
            Text(
                text = stringResource(
                    if (isEditMode) {
                        R.string.medicine_form_edit_intro
                    } else {
                        R.string.medicine_form_intro
                    },
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    text = stringResource(R.string.medicine_form_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            MedicineFormTextField(
                value = uiState.name,
                onValueChange = onNameChanged,
                label = stringResource(R.string.medicine_form_name_label),
                error = uiState.nameError.toMessage(),
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.packageForm,
                onValueChange = onPackageFormChanged,
                label = stringResource(R.string.medicine_form_package_form_label),
                supportingText = stringResource(R.string.medicine_form_package_form_support),
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.quantity,
                onValueChange = onQuantityChanged,
                label = stringResource(R.string.medicine_form_quantity_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                error = uiState.quantityError.toMessage(),
                keyboardType = KeyboardType.Decimal,
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.quantityUnit,
                onValueChange = onQuantityUnitChanged,
                label = stringResource(R.string.medicine_form_quantity_unit_label),
                supportingText = stringResource(R.string.medicine_form_quantity_unit_support),
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.lowStockThreshold,
                onValueChange = onLowStockThresholdChanged,
                label = stringResource(R.string.medicine_form_low_stock_threshold_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                error = uiState.lowStockThresholdError.toMessage(),
                keyboardType = KeyboardType.Decimal,
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.expirationDate,
                onValueChange = onExpirationDateChanged,
                label = stringResource(R.string.medicine_form_expiration_date_label),
                supportingText = stringResource(R.string.medicine_form_expiration_date_support),
                error = uiState.expirationDateError.toMessage(),
                keyboardType = KeyboardType.Number,
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.storageLocation,
                onValueChange = onStorageLocationChanged,
                label = stringResource(R.string.medicine_form_storage_location_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                enabled = !uiState.isLoading,
                singleLine = true,
            )

            MedicineFormTextField(
                value = uiState.notes,
                onValueChange = onNotesChanged,
                label = stringResource(R.string.medicine_form_notes_label),
                supportingText = stringResource(R.string.medicine_form_optional_support),
                enabled = !uiState.isLoading,
                minLines = MedicineFormDefaults.NotesMinLines,
            )

            if (uiState.isSaving) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Button(
                onClick = onSaveClick,
                enabled = !uiState.isSaving && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(
                        if (isEditMode) {
                            R.string.medicine_form_update_action
                        } else {
                            R.string.medicine_form_save_action
                        },
                    ),
                )
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
    enabled: Boolean = true,
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
        enabled = enabled,
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
 * Preview del form in modalita modifica con dati gia caricati.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineFormScreenEditPreview() {
    MediShelfTheme {
        MedicineFormScreen(
            uiState = MedicineFormUiState(
                mode = MedicineFormMode.Edit,
                name = "Ibuprofene",
                packageForm = "Sciroppo",
                quantity = "1",
                quantityUnit = "flacone",
                lowStockThreshold = "1",
                expirationDate = "2027-03-31",
                storageLocation = "Cucina",
                notes = "Da controllare prima delle vacanze.",
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
 * Preview dello stato di modifica quando il medicinale non esiste.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineFormScreenNotFoundPreview() {
    MediShelfTheme {
        MedicineFormScreen(
            uiState = MedicineFormUiState(
                mode = MedicineFormMode.Edit,
                isNotFound = true,
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
