package it.hendorsoftware.medishelf.feature.inventory

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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Route della schermata Inventario.
 *
 * @param onAddMedicineClick callback per aprire il form di inserimento.
 * @param onMedicineClick callback per aprire un dettaglio medicinale.
 * @param onArchiveClick callback per aprire l'archivio.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun InventoryRoute(
    onAddMedicineClick: () -> Unit,
    onMedicineClick: (String) -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    InventoryScreen(
        uiState = uiState,
        onAddMedicineClick = onAddMedicineClick,
        onMedicineClick = onMedicineClick,
        onArchiveClick = onArchiveClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchQueryCleared = viewModel::onSearchQueryCleared,
        modifier = modifier,
    )
}

/**
 * Schermata stateless dell'Inventario dei medicinali attivi.
 *
 * @param uiState stato completo della lista inventario.
 * @param onAddMedicineClick callback per aprire il form di inserimento.
 * @param onMedicineClick callback per aprire il dettaglio della voce selezionata.
 * @param onArchiveClick callback per aprire l'archivio.
 * @param onSearchQueryChanged callback per aggiornare la ricerca testuale.
 * @param onSearchQueryCleared callback per svuotare la ricerca.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun InventoryScreen(
    uiState: InventoryUiState,
    onAddMedicineClick: () -> Unit,
    onMedicineClick: (String) -> Unit,
    onArchiveClick: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchQueryCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(
                title = stringResource(R.string.inventory_screen_title),
                actions = {
                    IconButton(onClick = onArchiveClick) {
                        Icon(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = stringResource(
                                R.string.inventory_open_archive_content_description,
                            ),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMedicineClick,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                    )
                },
                text = {
                    Text(text = stringResource(R.string.navigation_action_add_medicine))
                },
            )
        },
    ) { innerPadding ->
        InventoryContent(
            uiState = uiState,
            onAddMedicineClick = onAddMedicineClick,
            onMedicineClick = onMedicineClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchQueryCleared = onSearchQueryCleared,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun InventoryContent(
    uiState: InventoryUiState,
    onAddMedicineClick: () -> Unit,
    onMedicineClick: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSearchQueryCleared: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (!uiState.isLoading) {
            InventorySearchField(
                query = uiState.searchQuery,
                onQueryChanged = onSearchQueryChanged,
                onClearClick = onSearchQueryCleared,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MediShelfDimens.ScreenPadding,
                        top = MediShelfDimens.ScreenPadding,
                        end = MediShelfDimens.ScreenPadding,
                    ),
            )
        }

        if (!uiState.isLoading && uiState.medicines.isEmpty()) {
            val isSearchEmptyState = uiState.searchQuery.isNotBlank() && uiState.hasActiveMedicines

            EmptyState(
                title = stringResource(
                    if (isSearchEmptyState) {
                        R.string.inventory_search_empty_title
                    } else {
                        R.string.inventory_empty_title
                    },
                ),
                body = stringResource(
                    if (isSearchEmptyState) {
                        R.string.inventory_search_empty_body
                    } else {
                        R.string.inventory_empty_body
                    },
                ),
                actionLabel = if (isSearchEmptyState) {
                    null
                } else {
                    stringResource(R.string.navigation_action_add_medicine)
                },
                onActionClick = if (isSearchEmptyState) null else onAddMedicineClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(MediShelfDimens.ScreenPadding),
                icon = {
                    Icon(
                        imageVector = if (isSearchEmptyState) {
                            Icons.Outlined.Search
                        } else {
                            Icons.Outlined.Inventory2
                        },
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
                bottom = MediShelfDimens.SpacingExtraLarge + MediShelfDimens.MinInteractiveSize,
            ),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        ) {
            items(
                items = uiState.medicines,
                key = InventoryMedicineItemUiModel::id,
            ) { medicine ->
                InventoryMedicineListItem(
                    medicine = medicine,
                    onClick = { onMedicineClick(medicine.id) },
                )
            }
        }
    }
}

@Composable
private fun InventorySearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier.testTag(InventoryTestTags.SearchField),
        label = {
            Text(text = stringResource(R.string.inventory_search_label))
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClearClick,
                    modifier = Modifier.testTag(InventoryTestTags.ClearSearchButton),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(
                            R.string.inventory_search_clear_content_description,
                        ),
                    )
                }
            }
        },
    )
}

/**
 * Item riusabile della lista inventario.
 *
 * @param medicine modello UI della voce da mostrare.
 * @param onClick callback per aprire il dettaglio.
 * @param modifier modificatore Compose applicato alla card.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InventoryMedicineListItem(
    medicine: InventoryMedicineItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(InventoryTestTags.medicineItem(medicine.id))
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

                MedicineStatusBadge(status = medicine.status)
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
            ) {
                medicine.expirationDate?.let { expirationDate ->
                    InventoryMedicineMetadata(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                            )
                        },
                        text = stringResource(
                            R.string.inventory_item_expiration,
                            expirationDate,
                        ),
                    )
                }
                medicine.quantity?.let { quantity ->
                    InventoryMedicineMetadata(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Inventory2,
                                contentDescription = null,
                            )
                        },
                        text = stringResource(R.string.inventory_item_quantity, quantity),
                    )
                }
                medicine.storageLocation?.let { storageLocation ->
                    InventoryMedicineMetadata(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                            )
                        },
                        text = stringResource(
                            R.string.inventory_item_location,
                            storageLocation,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryMedicineMetadata(
    icon: @Composable () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Preview dell'Inventario con medicinali attivi.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryScreenPreview() {
    MediShelfTheme {
        InventoryScreen(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = sampleInventoryMedicines,
            ),
            onAddMedicineClick = {},
            onMedicineClick = {},
            onArchiveClick = {},
            onSearchQueryChanged = {},
            onSearchQueryCleared = {},
        )
    }
}

/**
 * Preview dell'Inventario vuoto.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryScreenEmptyPreview() {
    MediShelfTheme {
        InventoryScreen(
            uiState = InventoryUiState(isLoading = false),
            onAddMedicineClick = {},
            onMedicineClick = {},
            onArchiveClick = {},
            onSearchQueryChanged = {},
            onSearchQueryCleared = {},
        )
    }
}

/**
 * Preview dell'Inventario senza risultati di ricerca.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryScreenSearchEmptyPreview() {
    MediShelfTheme {
        InventoryScreen(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = emptyList(),
                searchQuery = "tachipirina",
                hasActiveMedicines = true,
            ),
            onAddMedicineClick = {},
            onMedicineClick = {},
            onArchiveClick = {},
            onSearchQueryChanged = {},
            onSearchQueryCleared = {},
        )
    }
}

private val sampleInventoryMedicines = listOf(
    InventoryMedicineItemUiModel(
        id = "1",
        name = "Paracetamolo",
        packageForm = "Compresse",
        status = MedicineStatusBadgeStatus.Valid,
        expirationDate = "31/12/2026",
        quantity = "12 compresse",
        storageLocation = "Bagno",
    ),
    InventoryMedicineItemUiModel(
        id = "2",
        name = "Sciroppo tosse",
        packageForm = "Flacone",
        status = MedicineStatusBadgeStatus.ExpiringSoon,
        expirationDate = "05/06/2026",
        quantity = null,
        storageLocation = "Cucina",
    ),
    InventoryMedicineItemUiModel(
        id = "3",
        name = "Pomata lenitiva",
        packageForm = null,
        status = MedicineStatusBadgeStatus.NoExpiration,
        expirationDate = null,
        quantity = null,
        storageLocation = null,
    ),
)

/**
 * Test tag stabili della schermata Inventario.
 */
object InventoryTestTags {
    const val SearchField = "inventory_search_field"
    const val ClearSearchButton = "inventory_clear_search_button"

    /**
     * Restituisce il tag della card associata al medicinale.
     *
     * @param medicineId identificativo della voce visualizzata.
     * @return test tag stabile dell'item.
     */
    fun medicineItem(medicineId: String): String = "inventory_medicine_item_$medicineId"
}
