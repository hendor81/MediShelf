package it.hendorsoftware.medishelf

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
            .onNodeWithText(composeTestRule.activity.getString(R.string.app_name))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Inventario tramite bottom navigation.
     */
    @Test
    fun shouldNavigateFromHomeToInventory() {
        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.bottom_navigation_inventory_content_description,
                ),
            )
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.inventory_screen_title))
            .assertIsDisplayed()
    }

    /**
     * Controlla il ritorno Inventario -> Home tramite bottom navigation.
     */
    @Test
    fun shouldNavigateBackToHomeFromInventory() {
        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.bottom_navigation_inventory_content_description,
                ),
            )
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.inventory_screen_title))
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.bottom_navigation_home_content_description,
                ),
            )
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.app_name))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Scadenzario tramite bottom navigation.
     */
    @Test
    fun shouldNavigateFromHomeToExpiry() {
        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.bottom_navigation_expiry_content_description,
                ),
            )
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.expiry_screen_title))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Impostazioni tramite bottom navigation.
     */
    @Test
    fun shouldNavigateFromHomeToSettings() {
        composeTestRule
            .onNodeWithContentDescription(
                composeTestRule.activity.getString(
                    R.string.bottom_navigation_settings_content_description,
                ),
            )
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.settings_screen_title))
            .assertIsDisplayed()
    }

    /**
     * Controlla il passaggio Home -> Inserimento tramite CTA principale.
     */
    @Test
    fun shouldNavigateFromHomeToAddMedicine() {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.navigation_action_add_medicine))
            .performClick()

        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.medicine_add_screen_title))
            .assertIsDisplayed()
    }
}
