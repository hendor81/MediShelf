package it.hendorsoftware.medishelf.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verifica le azioni di recupero presentate dopo un rifiuto del permesso. */
class NotificationPermissionDialogTest {
    @get:Rule val composeTestRule = createComposeRule()

    /** Un blocco espone l'accesso alle impostazioni, senza richiedere nuovamente il permesso. */
    @Test fun shouldOfferSettingsForBlockedPermission() {
        var confirmations = 0
        composeTestRule.setContent {
            MediShelfTheme {
                NotificationPermissionDialog(true, { confirmations++ }, {})
            }
        }
        composeTestRule.onNodeWithText("Apri impostazioni notifiche").assertIsDisplayed().performClick()
        assertEquals(1, confirmations)
    }

    /** Il rifiuto temporaneo permette di riprovare oppure annullare senza abilitare. */
    @Test fun shouldAllowCancellingTemporaryDenial() {
        var confirmations = 0
        var dismissals = 0
        composeTestRule.setContent {
            MediShelfTheme {
                NotificationPermissionDialog(false, { confirmations++ }, { dismissals++ })
            }
        }
        composeTestRule.onNodeWithText("Riprova").assertIsDisplayed()
        composeTestRule.onNodeWithText("Annulla").performClick()
        assertEquals(0, confirmations)
        assertEquals(1, dismissals)
    }
}
