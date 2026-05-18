package it.hendorsoftware.medishelf.feature.medicineform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Rule
import org.junit.Test

/**
 * Test UI base del form di inserimento medicinale.
 */
class MedicineFormScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Verifica la presenza del campo nome e del pulsante di salvataggio.
     */
    @Test
    fun shouldShowNameFieldAndSaveButton() {
        setMedicineFormContent()

        composeTestRule
            .onNodeWithText("Nome medicinale")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Salva medicinale")
            .assertIsDisplayed()
    }

    /**
     * Verifica che il campo nome accetti l'inserimento testuale.
     */
    @Test
    fun shouldAllowTypingMedicineName() {
        setMedicineFormContent()

        composeTestRule
            .onNodeWithText("Nome medicinale")
            .performTextInput("Paracetamolo")

        composeTestRule
            .onNodeWithText("Paracetamolo")
            .assertIsDisplayed()
    }

    private fun setMedicineFormContent() {
        composeTestRule.setContent {
            var uiState by remember { mutableStateOf(MedicineFormUiState()) }

            MediShelfTheme {
                MedicineFormScreen(
                    uiState = uiState,
                    onNameChanged = { value -> uiState = uiState.copy(name = value) },
                    onPackageFormChanged = {},
                    onQuantityChanged = {},
                    onQuantityUnitChanged = {},
                    onLowStockThresholdChanged = {},
                    onExpirationDateChanged = {},
                    onStorageLocationChanged = {},
                    onNotesChanged = {},
                    onSaveClick = {},
                )
            }
        }
    }
}
