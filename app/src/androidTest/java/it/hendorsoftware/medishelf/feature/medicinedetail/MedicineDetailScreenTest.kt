package it.hendorsoftware.medishelf.feature.medicinedetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test UI base della schermata Dettaglio medicinale.
 */
class MedicineDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Verifica che i dati del medicinale e i testi neutri siano visibili.
     */
    @Test
    fun shouldShowMedicineDetailsAndNeutralMissingValues() {
        setMedicineDetailContent(
            uiState = MedicineDetailUiState(
                isLoading = false,
                medicine = sampleMedicineDetail.copy(
                    quantity = null,
                    expirationDate = null,
                ),
            ),
        )

        composeTestRule.onNodeWithText("Paracetamolo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Compresse").assertIsDisplayed()
        composeTestRule.onNodeWithText("Quantita non indicata").assertIsDisplayed()
        composeTestRule.onNodeWithText("Scadenza non indicata").assertIsDisplayed()
        composeTestRule.onNodeWithText("Archivia medicinale").assertIsDisplayed()
    }

    /**
     * Verifica che l'icona matita esponga il contratto verso la modifica.
     */
    @Test
    fun shouldRequestEditWhenEditIconIsClicked() {
        var editClicks = 0

        setMedicineDetailContent(onEditClick = { editClicks++ })

        composeTestRule
            .onNodeWithContentDescription("Modifica medicinale")
            .performClick()

        assertEquals(1, editClicks)
    }

    /**
     * Verifica che la cancellazione mostri un dialog e richieda conferma.
     */
    @Test
    fun shouldRequireConfirmationBeforeDelete() {
        var deleteRequested = false
        var deleteConfirmed = false

        setMedicineDetailContent(
            onDeleteClick = { deleteRequested = true },
            onDeleteConfirm = { deleteConfirmed = true },
        )

        composeTestRule
            .onNodeWithContentDescription("Elimina medicinale")
            .performClick()

        assertTrue(deleteRequested)
        assertFalse(deleteConfirmed)
    }

    /**
     * Verifica il contenuto del dialog di conferma quando lo stato lo richiede.
     */
    @Test
    fun shouldConfirmDeleteFromDialog() {
        var deleteConfirmed = false

        setMedicineDetailContent(
            uiState = MedicineDetailUiState(
                isLoading = false,
                medicine = sampleMedicineDetail,
                isDeleteDialogVisible = true,
            ),
            onDeleteConfirm = { deleteConfirmed = true },
        )

        composeTestRule
            .onNodeWithText("Eliminare definitivamente il medicinale?")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Elimina")
            .performClick()

        assertTrue(deleteConfirmed)
    }

    private fun setMedicineDetailContent(
        uiState: MedicineDetailUiState = MedicineDetailUiState(
            isLoading = false,
            medicine = sampleMedicineDetail,
        ),
        onEditClick: () -> Unit = {},
        onDeleteClick: () -> Unit = {},
        onDeleteConfirm: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MediShelfTheme {
                MedicineDetailScreen(
                    uiState = uiState,
                    onEditClick = onEditClick,
                    onArchiveClick = {},
                    onDeleteClick = onDeleteClick,
                    onDeleteConfirm = onDeleteConfirm,
                    onDeleteDismiss = {},
                    onNotFoundDoneClick = {},
                )
            }
        }
    }

    private val sampleMedicineDetail = MedicineDetailUiModel(
        id = "1",
        name = "Paracetamolo",
        packageForm = "Compresse",
        status = MedicineStatusBadgeStatus.Valid,
        quantity = "12 compresse",
        expirationDate = "31/12/2026",
        storageLocation = "Bagno",
        notes = "Confezione iniziata",
        isArchived = false,
    )
}
