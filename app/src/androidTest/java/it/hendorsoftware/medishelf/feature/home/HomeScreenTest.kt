package it.hendorsoftware.medishelf.feature.home

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
 * Test UI base della Home / Dashboard.
 */
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Verifica che le card principali mostrino i conteggi essenziali.
     */
    @Test
    fun shouldShowSummaryCards() {
        setHomeContent()

        composeTestRule
            .onNodeWithText("Attivi")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("12")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("In scadenza")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Scaduti")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Scorta bassa")
            .assertIsDisplayed()
    }

    /**
     * Verifica che la CTA principale esponga l'evento di aggiunta.
     */
    @Test
    fun shouldSendAddMedicineClick() {
        var addClicks = 0

        setHomeContent(onAddMedicineClick = { addClicks++ })

        composeTestRule
            .onNodeWithTag(HomeTestTags.AddMedicineButton)
            .performClick()

        assertEquals(1, addClicks)
    }

    /**
     * Verifica che il collegamento ricerca apra l'inventario.
     */
    @Test
    fun shouldOpenInventoryFromSearchShortcut() {
        var inventoryClicks = 0

        setHomeContent(onInventoryClick = { inventoryClicks++ })

        composeTestRule
            .onNodeWithTag(HomeTestTags.SearchShortcut)
            .performClick()

        assertEquals(1, inventoryClicks)
    }

    /**
     * Verifica che il tap su una voce in attenzione passi l'id al chiamante.
     */
    @Test
    fun shouldOpenAttentionItemDetail() {
        var openedMedicineId: String? = null

        setHomeContent(onAttentionItemClick = { medicineId -> openedMedicineId = medicineId })

        composeTestRule
            .onNodeWithTag(HomeTestTags.attentionItem("1"))
            .performClick()

        assertEquals("1", openedMedicineId)
    }

    private fun setHomeContent(
        onAddMedicineClick: () -> Unit = {},
        onInventoryClick: () -> Unit = {},
        onAttentionItemClick: (String) -> Unit = {},
    ) {
        composeTestRule.setContent {
            MediShelfTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        isLoading = false,
                        activeMedicineCount = 12,
                        expiringMedicineCount = 3,
                        expiredMedicineCount = 1,
                        lowStockMedicineCount = 2,
                        attentionItems = listOf(sampleAttentionItem),
                    ),
                    onAddMedicineClick = onAddMedicineClick,
                    onInventoryClick = onInventoryClick,
                    onExpiryClick = {},
                    onAttentionItemClick = onAttentionItemClick,
                )
            }
        }
    }

    private val sampleAttentionItem = HomeAttentionItemUiModel(
        id = "1",
        name = "Ibuprofene 400 mg",
        packageForm = "Compresse",
        expirationDate = "15/06/2026",
        quantity = "20 compresse",
        status = MedicineStatusBadgeStatus.ExpiringSoon,
    )
}
