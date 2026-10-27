package it.hendorsoftware.medishelf.feature.inventory

import androidx.compose.ui.test.assertContentDescriptionEquals
import it.hendorsoftware.medishelf.R
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import it.hendorsoftware.medishelf.setItalianContent
import org.junit.Rule
import org.junit.Test

/**
 * Test UI base della schermata Inventario.
 */
class InventoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Verifica lo stato vuoto con copy utile e azione di aggiunta.
     */
    @Test
    fun shouldShowEmptyState() {
        setInventoryContent(uiState = InventoryUiState(isLoading = false))

        composeTestRule
            .onNodeWithText("Inventario vuoto")
            .assertIsDisplayed()

        // La CTA fissa resta visibile; quella nel contenuto può richiedere scroll.
        val addActions = composeTestRule.onAllNodesWithText("Aggiungi medicinale")
        addActions.assertCountEquals(2)
        val inEmptyState = hasAnyAncestor(hasTestTag(InventoryTestTags.EmptyState))
        composeTestRule.onNode(hasText("Aggiungi medicinale") and !inEmptyState).assertIsDisplayed()
        composeTestRule.onNode(hasText("Aggiungi medicinale") and inEmptyState)
            .performScrollTo().assertIsDisplayed()
    }

    /**
     * Verifica che un item mostri stato, quantita, scadenza e luogo quando presenti.
     */
    @Test
    fun shouldShowMedicineItemDetails() {
        setInventoryContent(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = listOf(sampleMedicineItem),
            ),
        )

        composeTestRule
            .onNodeWithText("Paracetamolo")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNode(
                hasText("In scadenza") and hasAnyAncestor(hasTestTag(InventoryTestTags.medicineItem("1"))),
                useUnmergedTree = true,
            )
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Scadenza 31/05/2026")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Quantità 12 compresse")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Luogo Bagno")
            .performScrollTo()
            .assertIsDisplayed()
    }

    /**
     * Verifica che il tap sulla voce esponga l'id per la navigazione al dettaglio.
     */
    @Test
    fun shouldOpenDetailWhenMedicineItemIsClicked() {
        var openedMedicineId: String? = null

        setInventoryContent(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = listOf(sampleMedicineItem),
            ),
            onMedicineClick = { medicineId -> openedMedicineId = medicineId },
        )

        composeTestRule
            .onNodeWithTag(InventoryTestTags.medicineItem("1"))
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()

        assertEquals("1", openedMedicineId)
    }

    /**
     * Verifica che il campo ricerca inoltri il testo digitato al chiamante.
     */
    @Test
    fun shouldSendSearchQueryChanges() {
        var searchQuery = ""

        setInventoryContent(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = listOf(sampleMedicineItem),
            ),
            onSearchQueryChanged = { query -> searchQuery = query },
        )

        composeTestRule
            .onNodeWithTag(InventoryTestTags.SearchField)
            .performTextInput("para")

        assertEquals("para", searchQuery)
    }

    /**
     * Verifica lo stato vuoto dedicato quando la ricerca non trova medicinali.
     */
    @Test
    fun shouldShowSearchEmptyState() {
        setInventoryContent(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = emptyList(),
                searchQuery = "aspirina",
                hasActiveMedicines = true,
            ),
        )

        composeTestRule
            .onNodeWithText("Nessun risultato")
            .assertIsDisplayed()
    }

    /**
     * Verifica che il chip di filtro inoltri la selezione al chiamante.
     */
    @Test
    fun shouldSendStatusFilterSelection() {
        var selectedFilter: InventoryStatusFilter? = null

        setInventoryContent(
            uiState = InventoryUiState(
                isLoading = false,
                medicines = listOf(sampleMedicineItem),
            ),
            onStatusFilterSelected = { filter -> selectedFilter = filter },
        )

        composeTestRule
            .onNodeWithTag(InventoryTestTags.statusFilter(InventoryStatusFilter.Expired))
            .performClick()

        assertEquals(InventoryStatusFilter.Expired, selectedFilter)
    }

    /** L’etichetta accessibile resta disponibile anche quando il placeholder scompare. */
    @Test
    fun shouldKeepAccessibleSearchLabelWithQuery() {
        setInventoryContent(InventoryUiState(isLoading = false, searchQuery = "para"))
        val context = it.hendorsoftware.medishelf.italianTestContext()
        composeTestRule.onNodeWithTag(InventoryTestTags.SearchField)
            .assertContentDescriptionEquals(context.getString(R.string.inventory_search_label))
    }

    private fun setInventoryContent(
        uiState: InventoryUiState,
        onMedicineClick: (String) -> Unit = {},
        onSearchQueryChanged: (String) -> Unit = {},
        onStatusFilterSelected: (InventoryStatusFilter) -> Unit = {},
    ) {
        composeTestRule.setItalianContent {
            MediShelfTheme {
                InventoryScreen(
                    uiState = uiState,
                    onAddMedicineClick = {},
                    onMedicineClick = onMedicineClick,
                    onArchiveClick = {},
                    onSearchQueryChanged = onSearchQueryChanged,
                    onSearchQueryCleared = {},
                    onStatusFilterSelected = onStatusFilterSelected,
                )
            }
        }
    }

    private val sampleMedicineItem = InventoryMedicineItemUiModel(
        id = "1",
        name = "Paracetamolo",
        packageForm = "Compresse",
        status = MedicineStatusBadgeStatus.ExpiringSoon,
        expirationDate = "31/05/2026",
        quantity = "12 compresse",
        storageLocation = "Bagno",
    )
}
