package it.hendorsoftware.medishelf

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Smoke test UI della navigation shell.
 *
 * Verifica il criterio minimo della issue: l'app deve aprirsi sulla Home.
 */
@RunWith(AndroidJUnit4::class)
class NavigationSmokeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Controlla che la destinazione iniziale del grafo sia la Home.
     */
    @Test
    fun shouldOpenHomeOnLaunch() {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.home_screen_title))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Inventario tramite azione placeholder.
     */
    @Test
    fun shouldNavigateFromHomeToInventory() {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.navigation_action_open_inventory))
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.inventory_screen_title))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Scadenzario tramite azione placeholder.
     */
    @Test
    fun shouldNavigateFromHomeToExpiry() {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.navigation_action_open_expiry))
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.expiry_screen_title))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Impostazioni tramite azione placeholder.
     */
    @Test
    fun shouldNavigateFromHomeToSettings() {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.navigation_action_open_settings))
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.settings_screen_title))
            .assertIsDisplayed()
    }
}
