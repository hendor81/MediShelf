package it.hendorsoftware.medishelf.feature.archive

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.core.designsystem.component.MedicinePackageForms
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.BackNavigationButton
import it.hendorsoftware.medishelf.core.designsystem.component.EmptyState
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineListCard
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
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ArchiveViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ArchiveScreen(
        uiState = uiState,
        onMedicineClick = onMedicineClick,
        onBackClick = onBackClick,
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
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(
                title = stringResource(R.string.archive_screen_title),
                navigationIcon = { BackNavigationButton(onBackClick) },
            )
        },
    ) { innerPadding ->
        ArchiveContent(
            uiState = uiState,
            onMedicineClick = onMedicineClick,
            onBackClick = onBackClick,
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
    onBackClick: () -> Unit = {},
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
                actionLabel = stringResource(R.string.navigation_action_go_back),
                onActionClick = onBackClick,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .testTag(ArchiveTestTags.EmptyState)
                    .padding(MediShelfDimens.ScreenPadding),
                icon = {
                    Image(
                        painter = painterResource(R.drawable.archivio),
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

@Composable
private fun ArchiveMedicineListItem(
    medicine: ArchiveMedicineItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MedicineListCard(
        name = medicine.name,
        packageForm = medicine.packageForm,
        status = MedicineStatusBadgeStatus.Archived,
        metadata = listOfNotNull(
            MedicinePackageForms.labelFor(medicine.packageForm),
            medicine.expirationDate?.let { stringResource(R.string.inventory_item_expiration, it) },
            medicine.quantity?.let { stringResource(R.string.inventory_item_quantity, it) },
            medicine.storageLocation?.let { stringResource(R.string.inventory_item_location, it) },
        ),
        onClick = onClick,
        modifier = modifier.testTag(ArchiveTestTags.medicineItem(medicine.id)),
    )
}
/**
 * Test tag stabili della schermata Archivio.
 */
object ArchiveTestTags {
    const val EmptyState = "archive_empty_state"
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
