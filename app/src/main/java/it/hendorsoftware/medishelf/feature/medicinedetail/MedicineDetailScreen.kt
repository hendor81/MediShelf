package it.hendorsoftware.medishelf.feature.medicinedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.ConfirmDeleteDialog
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfPlaceholderScreen
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadge
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Route della schermata Dettaglio medicinale.
 *
 * @param medicineId identificativo minimo ricevuto dalla navigazione.
 * @param onEditClick callback per aprire la schermata di modifica.
 * @param onArchived callback invocata quando l'archiviazione termina.
 * @param onDeleted callback invocata quando la cancellazione termina.
 * @param onCloseClick callback per chiudere stati non recuperabili.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun MedicineDetailRoute(
    medicineId: String,
    onEditClick: () -> Unit,
    onArchived: () -> Unit,
    onDeleted: () -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MedicineDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(medicineId) {
        viewModel.loadMedicine(medicineId)
    }

    LaunchedEffect(uiState.hasArchiveCompleted) {
        if (uiState.hasArchiveCompleted) {
            onArchived()
        }
    }

    LaunchedEffect(uiState.hasDeleteCompleted) {
        if (uiState.hasDeleteCompleted) {
            onDeleted()
        }
    }

    MedicineDetailScreen(
        uiState = uiState,
        onEditClick = onEditClick,
        onAddQuantityClick = onEditClick,
        onQuantityIncrementClick = viewModel::onQuantityIncrementClick,
        onQuantityDecrementClick = viewModel::onQuantityDecrementClick,
        onQuantityFeedbackShown = viewModel::onQuantityFeedbackShown,
        onArchiveClick = viewModel::onArchiveClick,
        onDeleteClick = viewModel::onDeleteClick,
        onDeleteConfirm = viewModel::onDeleteConfirmed,
        onDeleteDismiss = viewModel::onDeleteDismissed,
        onNotFoundDoneClick = onCloseClick,
        modifier = modifier,
    )
}

/**
 * Schermata stateless del dettaglio medicinale.
 *
 * @param uiState stato completo del dettaglio.
 * @param onEditClick callback per aprire la modifica tramite icona matita.
 * @param onAddQuantityClick callback esplicita per impostare la quantita assente.
 * @param onQuantityIncrementClick callback per aumentare rapidamente la quantita.
 * @param onQuantityDecrementClick callback per ridurre rapidamente la quantita.
 * @param onQuantityFeedbackShown callback per consumare il feedback discreto.
 * @param onArchiveClick callback per archiviare la voce.
 * @param onDeleteClick callback per aprire la conferma di cancellazione.
 * @param onDeleteConfirm callback invocata dal dialog dopo conferma esplicita.
 * @param onDeleteDismiss callback invocata quando il dialog viene annullato.
 * @param onNotFoundDoneClick callback per uscire dallo stato non trovato.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun MedicineDetailScreen(
    uiState: MedicineDetailUiState,
    onEditClick: () -> Unit,
    onAddQuantityClick: () -> Unit,
    onQuantityIncrementClick: () -> Unit,
    onQuantityDecrementClick: () -> Unit,
    onQuantityFeedbackShown: () -> Unit,
    onArchiveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    onNotFoundDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasMedicine = uiState.medicine != null
    val snackbarHostState = remember { SnackbarHostState() }
    val quantityFeedbackMessage = uiState.quantityFeedback?.toMessage()

    LaunchedEffect(quantityFeedbackMessage) {
        if (quantityFeedbackMessage != null) {
            snackbarHostState.showSnackbar(quantityFeedbackMessage)
            onQuantityFeedbackShown()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MediShelfTopAppBar(
                title = stringResource(R.string.medicine_detail_screen_title),
                actions = {
                    IconButton(
                        onClick = onEditClick,
                        enabled = hasMedicine && !uiState.isActionInProgress,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.navigation_action_edit_medicine),
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        enabled = hasMedicine && !uiState.isActionInProgress,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.medicine_detail_delete_content_description),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        MedicineDetailContent(
            uiState = uiState,
            onAddQuantityClick = onAddQuantityClick,
            onQuantityIncrementClick = onQuantityIncrementClick,
            onQuantityDecrementClick = onQuantityDecrementClick,
            onArchiveClick = onArchiveClick,
            onNotFoundDoneClick = onNotFoundDoneClick,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }

    if (uiState.isDeleteDialogVisible) {
        ConfirmDeleteDialog(
            title = stringResource(R.string.medicine_detail_delete_dialog_title),
            body = stringResource(R.string.medicine_detail_delete_dialog_body),
            confirmLabel = stringResource(R.string.designsystem_delete_dialog_confirm),
            dismissLabel = stringResource(R.string.designsystem_delete_dialog_dismiss),
            onConfirm = onDeleteConfirm,
            onDismiss = onDeleteDismiss,
        )
    }
}

@Composable
private fun MedicineDetailContent(
    uiState: MedicineDetailUiState,
    onAddQuantityClick: () -> Unit,
    onQuantityIncrementClick: () -> Unit,
    onQuantityDecrementClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onNotFoundDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> MedicineDetailLoading(modifier = modifier)
        uiState.isNotFound -> MedicineDetailNotFound(
            onDoneClick = onNotFoundDoneClick,
            modifier = modifier,
        )
        uiState.medicine != null -> MedicineDetailData(
            medicine = uiState.medicine,
            isActionInProgress = uiState.isActionInProgress,
            onAddQuantityClick = onAddQuantityClick,
            onQuantityIncrementClick = onQuantityIncrementClick,
            onQuantityDecrementClick = onQuantityDecrementClick,
            onArchiveClick = onArchiveClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun MedicineDetailLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(MediShelfDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
    ) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Text(
            text = stringResource(R.string.medicine_detail_loading),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MedicineDetailNotFound(
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MediShelfPlaceholderScreen(
        title = stringResource(R.string.medicine_detail_not_found_title),
        body = stringResource(R.string.medicine_detail_not_found_body),
        modifier = modifier,
    ) {
        Button(
            onClick = onDoneClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.navigation_action_go_back))
        }
    }
}

@Composable
private fun MedicineDetailData(
    medicine: MedicineDetailUiModel,
    isActionInProgress: Boolean,
    onAddQuantityClick: () -> Unit,
    onQuantityIncrementClick: () -> Unit,
    onQuantityDecrementClick: () -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(MediShelfDimens.ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
    ) {
        Card(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MediShelfDimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
                    ) {
                        Text(
                            text = medicine.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        medicine.packageForm?.let { packageForm ->
                            Text(
                                text = packageForm,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    MedicineStatusBadge(status = medicine.status)
                }
            }
        }

        MedicineDetailInfoCard(
            title = stringResource(R.string.medicine_detail_main_data_title),
        ) {
            MedicineDetailQuantityRow(
                quantity = medicine.quantity,
                isQuantityAtZero = medicine.isQuantityAtZero,
                isActionInProgress = isActionInProgress,
                onAddQuantityClick = onAddQuantityClick,
                onQuantityIncrementClick = onQuantityIncrementClick,
                onQuantityDecrementClick = onQuantityDecrementClick,
            )
            MedicineDetailInfoRow(
                icon = Icons.Outlined.CalendarMonth,
                label = stringResource(R.string.medicine_detail_expiration_label),
                value = medicine.expirationDate ?: stringResource(
                    R.string.medicine_detail_expiration_not_set,
                ),
            )
            MedicineDetailInfoRow(
                icon = Icons.Outlined.LocationOn,
                label = stringResource(R.string.medicine_detail_storage_location_label),
                value = medicine.storageLocation ?: stringResource(
                    R.string.medicine_detail_storage_location_not_set,
                ),
            )
        }

        MedicineDetailInfoCard(
            title = stringResource(R.string.medicine_detail_notes_title),
        ) {
            MedicineDetailInfoRow(
                icon = Icons.AutoMirrored.Outlined.Notes,
                label = stringResource(R.string.medicine_detail_notes_label),
                value = medicine.notes ?: stringResource(R.string.medicine_detail_notes_not_set),
            )
        }

        Button(
            onClick = onArchiveClick,
            enabled = !medicine.isArchived && !isActionInProgress,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(MedicineDetailTestTags.ARCHIVE_BUTTON),
        ) {
            Icon(
                imageVector = Icons.Outlined.Archive,
                contentDescription = null,
            )
            Text(
                text = stringResource(
                    if (medicine.isArchived) {
                        R.string.medicine_detail_already_archived_action
                    } else {
                        R.string.medicine_detail_archive_action
                    },
                ),
                modifier = Modifier.padding(start = MediShelfDimens.SpacingSmall),
            )
        }
    }
}

@Composable
private fun MedicineDetailQuantityRow(
    quantity: String?,
    isQuantityAtZero: Boolean,
    isActionInProgress: Boolean,
    onAddQuantityClick: () -> Unit,
    onQuantityIncrementClick: () -> Unit,
    onQuantityDecrementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Outlined.Inventory2,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
        ) {
            Text(
                text = stringResource(R.string.medicine_detail_quantity_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = quantity ?: stringResource(R.string.medicine_detail_quantity_not_set),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (quantity == null) {
                OutlinedButton(
                    onClick = onAddQuantityClick,
                    enabled = !isActionInProgress,
                    modifier = Modifier.testTag(MedicineDetailTestTags.ADD_QUANTITY_BUTTON),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.medicine_detail_quantity_add_action),
                        modifier = Modifier.padding(start = MediShelfDimens.SpacingSmall),
                    )
                }
            }
        }

        if (quantity != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall)) {
                IconButton(
                    onClick = onQuantityDecrementClick,
                    enabled = !isActionInProgress && !isQuantityAtZero,
                    modifier = Modifier.testTag(MedicineDetailTestTags.DECREMENT_QUANTITY_BUTTON),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Remove,
                        contentDescription = stringResource(
                            R.string.medicine_detail_quantity_decrement_content_description,
                        ),
                    )
                }
                IconButton(
                    onClick = onQuantityIncrementClick,
                    enabled = !isActionInProgress,
                    modifier = Modifier.testTag(MedicineDetailTestTags.INCREMENT_QUANTITY_BUTTON),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(
                            R.string.medicine_detail_quantity_increment_content_description,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun MedicineDetailInfoCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(MediShelfDimens.SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            content()
        }
    }
}

@Composable
private fun MedicineDetailInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Test tag stabili della schermata Dettaglio medicinale.
 */
object MedicineDetailTestTags {
    const val ARCHIVE_BUTTON = "medicine_detail_archive_button"
    const val ADD_QUANTITY_BUTTON = "medicine_detail_add_quantity_button"
    const val INCREMENT_QUANTITY_BUTTON = "medicine_detail_increment_quantity_button"
    const val DECREMENT_QUANTITY_BUTTON = "medicine_detail_decrement_quantity_button"
}

@Composable
private fun MedicineDetailQuantityFeedback.toMessage(): String = stringResource(
    when (this) {
        MedicineDetailQuantityFeedback.Updated -> R.string.medicine_detail_quantity_updated
        MedicineDetailQuantityFeedback.MissingQuantity ->
            R.string.medicine_detail_quantity_missing_feedback
        MedicineDetailQuantityFeedback.AlreadyZero -> R.string.medicine_detail_quantity_zero_feedback
    },
)

/**
 * Preview del Dettaglio medicinale con dati completi.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineDetailScreenPreview() {
    MediShelfTheme {
        MedicineDetailScreen(
            uiState = MedicineDetailUiState(
                isLoading = false,
                medicine = sampleMedicineDetail,
            ),
            onEditClick = {},
            onAddQuantityClick = {},
            onQuantityIncrementClick = {},
            onQuantityDecrementClick = {},
            onQuantityFeedbackShown = {},
            onArchiveClick = {},
            onDeleteClick = {},
            onDeleteConfirm = {},
            onDeleteDismiss = {},
            onNotFoundDoneClick = {},
        )
    }
}

/**
 * Preview del Dettaglio medicinale con campi opzionali assenti.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineDetailScreenMissingDataPreview() {
    MediShelfTheme {
        MedicineDetailScreen(
            uiState = MedicineDetailUiState(
                isLoading = false,
                medicine = sampleMedicineDetail.copy(
                    quantity = null,
                    expirationDate = null,
                    storageLocation = null,
                    notes = null,
                    status = MedicineStatusBadgeStatus.NoExpiration,
                ),
            ),
            onEditClick = {},
            onAddQuantityClick = {},
            onQuantityIncrementClick = {},
            onQuantityDecrementClick = {},
            onQuantityFeedbackShown = {},
            onArchiveClick = {},
            onDeleteClick = {},
            onDeleteConfirm = {},
            onDeleteDismiss = {},
            onNotFoundDoneClick = {},
        )
    }
}

/**
 * Preview dello stato medicinale non trovato.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineDetailScreenNotFoundPreview() {
    MediShelfTheme {
        MedicineDetailScreen(
            uiState = MedicineDetailUiState(
                isLoading = false,
                isNotFound = true,
            ),
            onEditClick = {},
            onAddQuantityClick = {},
            onQuantityIncrementClick = {},
            onQuantityDecrementClick = {},
            onQuantityFeedbackShown = {},
            onArchiveClick = {},
            onDeleteClick = {},
            onDeleteConfirm = {},
            onDeleteDismiss = {},
            onNotFoundDoneClick = {},
        )
    }
}

private val sampleMedicineDetail = MedicineDetailUiModel(
    id = "1",
    name = "Paracetamolo",
    packageForm = "Compresse",
    status = MedicineStatusBadgeStatus.Valid,
    quantity = "12 compresse",
    isQuantityAtZero = false,
    expirationDate = "31/12/2026",
    storageLocation = "Bagno",
    notes = "Confezione iniziata.",
    isArchived = false,
)
