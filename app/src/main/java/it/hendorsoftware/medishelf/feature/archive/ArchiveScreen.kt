package it.hendorsoftware.medishelf.feature.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.EmptyState
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadge
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Route della schermata Archivio.
 *
 * @param onMedicineClick callback per aprire il dettaglio di una voce archiviata.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun ArchiveRoute(
    onMedicineClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArchiveViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ArchiveScreen(
        uiState = uiState,
        onMedicineClick = onMedicineClick,
        modifier = modifier,
    )
}

/**
 * Schermata stateless dell'Archivio dei medicinali non piu attivi.
 *
 * @param uiState stato completo della lista archivio.
 * @param onMedicineClick callback per aprire il dettaglio della voce selezionata.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun ArchiveScreen(
    uiState: ArchiveUiState,
    onMedicineClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.archive_screen_title))
        },
    ) { innerPadding ->
        ArchiveContent(
            uiState = uiState,
            onMedicineClick = onMedicineClick,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun ArchiveContent(
    uiState: ArchiveUiState,
    onMedicineClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (!uiState.isLoading && uiState.medicines.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.archive_empty_title),
                body = stringResource(R.string.archive_empty_body),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MediShelfDimens.ScreenPadding),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = null,
                    )
                },
            )
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = MediShelfDimens.ScreenPadding,
                top = MediShelfDimens.ScreenPadding,
                end = MediShelfDimens.ScreenPadding,
                bottom = MediShelfDimens.ScreenPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        ) {
            item {
                Text(
                    text = stringResource(R.string.archive_secondary_area_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            items(
                items = uiState.medicines,
                key = ArchiveMedicineItemUiModel::id,
            ) { medicine ->
                ArchiveMedicineListItem(
                    medicine = medicine,
                    onClick = { onMedicineClick(medicine.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ArchiveMedicineListItem(
    medicine: ArchiveMedicineItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(ArchiveTestTags.medicineItem(medicine.id))
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(MediShelfDimens.SpacingMedium),
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    medicine.packageForm?.let { packageForm ->
                        Text(
                            text = packageForm,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                MedicineStatusBadge(status = MedicineStatusBadgeStatus.Archived)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
            ) {
                medicine.expirationDate?.let { expirationDate ->
                    ArchiveMedicineMetadata(
                        icon = Icons.Outlined.CalendarMonth,
                        text = stringResource(R.string.inventory_item_expiration, expirationDate),
                    )
                }
                medicine.quantity?.let { quantity ->
                    ArchiveMedicineMetadata(
                        icon = Icons.Outlined.Inventory2,
                        text = stringResource(R.string.inventory_item_quantity, quantity),
                    )
                }
                medicine.storageLocation?.let { storageLocation ->
                    ArchiveMedicineMetadata(
                        icon = Icons.Outlined.LocationOn,
                        text = stringResource(R.string.inventory_item_location, storageLocation),
                    )
                }
            }
        }
    }
}

@Composable
private fun ArchiveMedicineMetadata(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Test tag stabili della schermata Archivio.
 */
object ArchiveTestTags {
    /**
     * Restituisce il tag della card associata al medicinale archiviato.
     *
     * @param medicineId identificativo della voce visualizzata.
     * @return test tag stabile dell'item.
     */
    fun medicineItem(medicineId: String): String = "archive_medicine_item_$medicineId"
}

/**
 * Preview dell'Archivio con medicinali archiviati.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveScreenPreview() {
    MediShelfTheme {
        ArchiveScreen(
            uiState = ArchiveUiState(
                isLoading = false,
                medicines = sampleArchiveMedicines,
            ),
            onMedicineClick = {},
        )
    }
}

/**
 * Preview dell'Archivio vuoto.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveScreenEmptyPreview() {
    MediShelfTheme {
        ArchiveScreen(
            uiState = ArchiveUiState(isLoading = false),
            onMedicineClick = {},
        )
    }
}

/**
 * Preview del contenuto Archivio senza top app bar.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveContentPreview() {
    MediShelfTheme {
        ArchiveContent(
            uiState = ArchiveUiState(
                isLoading = false,
                medicines = sampleArchiveMedicines,
            ),
            onMedicineClick = {},
        )
    }
}

/**
 * Preview del contenuto Archivio in stato vuoto.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveContentEmptyPreview() {
    MediShelfTheme {
        ArchiveContent(
            uiState = ArchiveUiState(isLoading = false),
            onMedicineClick = {},
        )
    }
}

/**
 * Preview di una card medicinale archiviato.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveMedicineListItemPreview() {
    MediShelfTheme {
        ArchiveMedicineListItem(
            medicine = sampleArchiveMedicines.first(),
            onClick = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview di un metadato dell'Archivio.
 */
@Preview(showBackground = true)
@Composable
private fun ArchiveMedicineMetadataPreview() {
    MediShelfTheme {
        ArchiveMedicineMetadata(
            icon = Icons.Outlined.CalendarMonth,
            text = "Scadenza 05/06/2026",
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

private val sampleArchiveMedicines = listOf(
    ArchiveMedicineItemUiModel(
        id = "1",
        name = "Sciroppo tosse",
        packageForm = "Flacone",
        expirationDate = "05/06/2026",
        quantity = null,
        storageLocation = "Cucina",
    ),
    ArchiveMedicineItemUiModel(
        id = "2",
        name = "Pomata lenitiva",
        packageForm = null,
        expirationDate = null,
        quantity = "1 confezione",
        storageLocation = "Bagno",
    ),
)
