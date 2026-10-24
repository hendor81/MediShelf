package it.hendorsoftware.medishelf.feature.inventory

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.AddMedicineActionBar
import it.hendorsoftware.medishelf.core.designsystem.component.EmptyState
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineListCard
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
    initialFilter: InventoryStatusFilter = InventoryStatusFilter.All,
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(initialFilter) { viewModel.applyInitialFilter(initialFilter) }

    InventoryScreen(
        uiState = uiState,
        onAddMedicineClick = onAddMedicineClick,
        onMedicineClick = onMedicineClick,
        onArchiveClick = onArchiveClick,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onSearchQueryCleared = viewModel::onSearchQueryCleared,
        onStatusFilterSelected = viewModel::onStatusFilterSelected,
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
 * @param onStatusFilterSelected callback per aggiornare il filtro di stato.
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
    onStatusFilterSelected: (InventoryStatusFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(
                title = stringResource(R.string.inventory_screen_title),
                prominentTitle = true,
                actions = {
                    IconButton(onClick = onArchiveClick) {
                        Image(
                            painter = painterResource(R.drawable.archivio),
                            modifier = Modifier.size(MediShelfDimens.BottomNavigationIconSize),
                            contentDescription = stringResource(
                                R.string.inventory_open_archive_content_description,
                            ),
                        )
                    }
                },
            )
        },
        bottomBar = { AddMedicineActionBar(onAddMedicineClick = onAddMedicineClick) },
    ) { innerPadding ->
        InventoryContent(
            uiState = uiState,
            onAddMedicineClick = onAddMedicineClick,
            onMedicineClick = onMedicineClick,
            onSearchQueryChanged = onSearchQueryChanged,
            onSearchQueryCleared = onSearchQueryCleared,
            onStatusFilterSelected = onStatusFilterSelected,
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
    onStatusFilterSelected: (InventoryStatusFilter) -> Unit,
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

            InventoryStatusFilterChips(
                selectedFilter = uiState.selectedStatusFilter,
                onFilterSelected = onStatusFilterSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MediShelfDimens.ScreenPadding,
                        top = MediShelfDimens.SpacingSmall,
                        end = MediShelfDimens.ScreenPadding,
                    ),
            )
        }

        if (!uiState.isLoading && uiState.medicines.isEmpty()) {
            val isFilteredEmptyState = (
                uiState.searchQuery.isNotBlank() ||
                    uiState.selectedStatusFilter != InventoryStatusFilter.All
                ) && uiState.hasActiveMedicines

            EmptyState(
                title = stringResource(
                    if (isFilteredEmptyState) {
                        R.string.inventory_search_empty_title
                    } else {
                        R.string.inventory_empty_title
                    },
                ),
                body = stringResource(
                    if (isFilteredEmptyState) {
                        R.string.inventory_search_empty_body
                    } else {
                        R.string.inventory_empty_body
                    },
                ),
                actionLabel = if (isFilteredEmptyState) {
                    null
                } else {
                    stringResource(R.string.navigation_action_add_medicine)
                },
                onActionClick = if (isFilteredEmptyState) null else onAddMedicineClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(MediShelfDimens.ScreenPadding),
                icon = {
                    if (isFilteredEmptyState) {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                    } else {
                        Image(painterResource(R.drawable.inventario), contentDescription = null)
                    }
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
    val searchLabel = stringResource(R.string.inventory_search_label)
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier
            .testTag(InventoryTestTags.SearchField)
            .semantics { contentDescription = searchLabel }
            .border(
                width = MediShelfDimens.SearchBorderWidth,
                color = MaterialTheme.colorScheme.outline.copy(alpha = MediShelfDimens.SearchBorderAlpha),
                shape = RoundedCornerShape(MediShelfDimens.CornerLarge),
            ),
        shape = RoundedCornerShape(MediShelfDimens.CornerLarge),
        textStyle = MaterialTheme.typography.bodyLarge,
        // Il bordo condiviso resta sottile anche con il focus, come la ricerca Home.
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        placeholder = {
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InventoryStatusFilterChips(
    selectedFilter: InventoryStatusFilter,
    onFilterSelected: (InventoryStatusFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
    ) {
        InventoryStatusFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(text = stringResource(filter.labelResId))
                },
                modifier = Modifier.testTag(InventoryTestTags.statusFilter(filter)),
            )
        }
    }
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
    MedicineListCard(
        name = medicine.name,
        packageForm = medicine.packageForm,
        status = medicine.status,
        isLowStock = medicine.isLowStock,
        metadata = listOfNotNull(
            medicine.packageForm,
            medicine.expirationDate?.let { stringResource(R.string.inventory_item_expiration, it) },
            medicine.quantity?.let { stringResource(R.string.inventory_item_quantity, it) },
            medicine.storageLocation?.let { stringResource(R.string.inventory_item_location, it) },
        ),
        onClick = onClick,
        modifier = modifier.testTag(InventoryTestTags.medicineItem(medicine.id)),
    )
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
            onStatusFilterSelected = {},
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
            onStatusFilterSelected = {},
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
            onStatusFilterSelected = {},
        )
    }
}

/**
 * Preview del contenuto Inventario senza scaffold.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryContentPreview() {
    MediShelfTheme {
        InventoryContent(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = sampleInventoryMedicines,
            ),
            onAddMedicineClick = {},
            onMedicineClick = {},
            onSearchQueryChanged = {},
            onSearchQueryCleared = {},
            onStatusFilterSelected = {},
        )
    }
}

/**
 * Preview del campo ricerca Inventario.
 */
@Preview(showBackground = true)
@Composable
private fun InventorySearchFieldPreview() {
    MediShelfTheme {
        InventorySearchField(
            query = "para",
            onQueryChanged = {},
            onClearClick = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview dei filtri di stato Inventario.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryStatusFilterChipsPreview() {
    MediShelfTheme {
        InventoryStatusFilterChips(
            selectedFilter = InventoryStatusFilter.ExpiringSoon,
            onFilterSelected = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview di una card medicinale dell'Inventario.
 */
@Preview(showBackground = true)
@Composable
private fun InventoryMedicineListItemPreview() {
    MediShelfTheme {
        InventoryMedicineListItem(
            medicine = sampleInventoryMedicines.first(),
            onClick = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
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
     * Restituisce il tag del filtro di stato richiesto.
     *
     * @param filter filtro visualizzato.
     * @return test tag stabile del chip.
     */
    fun statusFilter(filter: InventoryStatusFilter): String = "inventory_status_filter_${filter.name}"

    /**
     * Restituisce il tag della card associata al medicinale.
     *
     * @param medicineId identificativo della voce visualizzata.
     * @return test tag stabile dell'item.
     */
    fun medicineItem(medicineId: String): String = "inventory_medicine_item_$medicineId"
}
