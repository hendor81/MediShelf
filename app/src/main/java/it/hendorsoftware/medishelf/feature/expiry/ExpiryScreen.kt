package it.hendorsoftware.medishelf.feature.expiry

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
 * Route della schermata Scadenzario.
 *
 * @param onMedicineClick callback per aprire un dettaglio medicinale.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun ExpiryRoute(
    onMedicineClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpiryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ExpiryScreen(
        uiState = uiState,
        onMedicineClick = onMedicineClick,
        modifier = modifier,
    )
}

/**
 * Schermata stateless dello Scadenzario.
 *
 * @param uiState stato completo delle sezioni di scadenza.
 * @param onMedicineClick callback per aprire il dettaglio della voce selezionata.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun ExpiryScreen(
    uiState: ExpiryUiState,
    onMedicineClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.expiry_screen_title))
        },
    ) { innerPadding ->
        ExpiryContent(
            uiState = uiState,
            onMedicineClick = onMedicineClick,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun ExpiryContent(
    uiState: ExpiryUiState,
    onMedicineClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        if (!uiState.isLoading && uiState.isEmpty) {
            EmptyState(
                title = stringResource(R.string.expiry_empty_title),
                body = stringResource(R.string.expiry_empty_body),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(MediShelfDimens.ScreenPadding),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
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
                bottom = MediShelfDimens.SpacingExtraLarge,
            ),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingLarge),
        ) {
            expirySection(
                title = R.string.expiry_section_expiring_title,
                body = R.string.expiry_section_expiring_body,
                sectionTag = ExpiryTestTags.ExpiringSection,
                medicines = uiState.expiringMedicines,
                onMedicineClick = onMedicineClick,
            )
            expirySection(
                title = R.string.expiry_section_expired_title,
                body = R.string.expiry_section_expired_body,
                sectionTag = ExpiryTestTags.ExpiredSection,
                medicines = uiState.expiredMedicines,
                onMedicineClick = onMedicineClick,
            )
            expirySection(
                title = R.string.expiry_section_no_expiration_title,
                body = R.string.expiry_section_no_expiration_body,
                sectionTag = ExpiryTestTags.NoExpirationSection,
                medicines = uiState.noExpirationMedicines,
                onMedicineClick = onMedicineClick,
            )
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.expirySection(
    @StringRes title: Int,
    @StringRes body: Int,
    sectionTag: String,
    medicines: List<ExpiryMedicineItemUiModel>,
    onMedicineClick: (String) -> Unit,
) {
    if (medicines.isEmpty()) {
        return
    }

    item(key = sectionTag) {
        ExpirySectionHeader(
            title = stringResource(title),
            body = stringResource(body),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(sectionTag),
        )
    }

    items(
        items = medicines,
        key = ExpiryMedicineItemUiModel::id,
    ) { medicine ->
        ExpiryMedicineListItem(
            medicine = medicine,
            onClick = { onMedicineClick(medicine.id) },
        )
    }
}

@Composable
private fun ExpirySectionHeader(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Item dello Scadenzario con metadati essenziali e accesso al dettaglio.
 *
 * @param medicine modello UI della voce da mostrare.
 * @param onClick callback per aprire il dettaglio.
 * @param modifier modificatore Compose applicato alla card.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpiryMedicineListItem(
    medicine: ExpiryMedicineItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(ExpiryTestTags.medicineItem(medicine.id))
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
                    ExpiryMedicineMetadata(
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
                    ExpiryMedicineMetadata(
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
                    ExpiryMedicineMetadata(
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
private fun ExpiryMedicineMetadata(
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
 * Preview dello Scadenzario con tutte le sezioni valorizzate.
 */
@Preview(showBackground = true)
@Composable
private fun ExpiryScreenPreview() {
    MediShelfTheme {
        ExpiryScreen(
            uiState = ExpiryUiState(
                isLoading = false,
                expiringMedicines = listOf(sampleExpiryMedicines[0]),
                expiredMedicines = listOf(sampleExpiryMedicines[1]),
                noExpirationMedicines = listOf(sampleExpiryMedicines[2]),
            ),
            onMedicineClick = {},
        )
    }
}

/**
 * Preview dello Scadenzario vuoto.
 */
@Preview(showBackground = true)
@Composable
private fun ExpiryScreenEmptyPreview() {
    MediShelfTheme {
        ExpiryScreen(
            uiState = ExpiryUiState(isLoading = false),
            onMedicineClick = {},
        )
    }
}

private val sampleExpiryMedicines = listOf(
    ExpiryMedicineItemUiModel(
        id = "1",
        name = "Sciroppo tosse",
        packageForm = "Flacone",
        status = MedicineStatusBadgeStatus.ExpiringSoon,
        expirationDate = "05/06/2026",
        quantity = null,
        storageLocation = "Cucina",
    ),
    ExpiryMedicineItemUiModel(
        id = "2",
        name = "Paracetamolo",
        packageForm = "Compresse",
        status = MedicineStatusBadgeStatus.Expired,
        expirationDate = "01/05/2026",
        quantity = "4 compresse",
        storageLocation = "Bagno",
    ),
    ExpiryMedicineItemUiModel(
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
 * Test tag stabili della schermata Scadenzario.
 */
object ExpiryTestTags {
    const val ExpiringSection = "expiry_expiring_section"
    const val ExpiredSection = "expiry_expired_section"
    const val NoExpirationSection = "expiry_no_expiration_section"

    /**
     * Restituisce il tag della card associata al medicinale.
     *
     * @param medicineId identificativo della voce visualizzata.
     * @return test tag stabile dell'item.
     */
    fun medicineItem(medicineId: String): String = "expiry_medicine_item_$medicineId"
}
