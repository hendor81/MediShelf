package it.hendorsoftware.medishelf.feature.inventory

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
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

        composeTestRule
            .onNodeWithText("Aggiungi medicinale")
            .assertIsDisplayed()
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
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("In scadenza")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Scadenza 31/05/2026")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Quantita 12 compresse")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Luogo Bagno")
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
            .performClick()

        assertEquals("1", openedMedicineId)
    }

    private fun setInventoryContent(
        uiState: InventoryUiState,
        onMedicineClick: (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            MediShelfTheme {
                InventoryScreen(
                    uiState = uiState,
                    onAddMedicineClick = {},
                    onMedicineClick = onMedicineClick,
                    onArchiveClick = {},
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
