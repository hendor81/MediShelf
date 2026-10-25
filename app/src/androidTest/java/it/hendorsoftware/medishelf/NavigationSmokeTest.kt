package it.hendorsoftware.medishelf

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Smoke test UI della navigation shell.
 *
 * Verifica il criterio minimo della issue: l'app deve aprirsi sulla Home.
 */
@RunWith(AndroidJUnit4::class)
class NavigationSmokeTest {

    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /** Concede il permesso prima di avviare l'Activity: nessun dialogo Android copre i test. */
    @get:Rule
    val rules: RuleChain = RuleChain.outerRule(object : ExternalResource() {
        override fun before() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val instrumentation = InstrumentationRegistry.getInstrumentation()
                instrumentation.uiAutomation.grantRuntimePermission(
                    instrumentation.targetContext.packageName, Manifest.permission.POST_NOTIFICATIONS,
                )
            }
        }
    }).around(composeTestRule)

    /** I test del grafo principale chiudono l'introduzione se presente. */
    @Before
    fun completeOnboardingIfNeeded() {
        val title = composeTestRule.activity.getString(R.string.onboarding_title)
        val home = composeTestRule.activity.getString(R.string.bottom_navigation_home)
        composeTestRule.waitUntil(5_000) {
            composeTestRule.onAllNodes(hasText(title)
                .or(hasText(home) and hasClickAction())).fetchSemanticsNodes().isNotEmpty()
        }
        if (composeTestRule.onAllNodes(androidx.compose.ui.test.hasText(title)).fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.onboarding_skip))
                .performScrollTo().performClick()
            composeTestRule.waitForIdle()
        }
    }

    /**
     * Controlla che la destinazione iniziale del grafo sia la Home.
     */
    @Test
    fun shouldOpenHomeOnLaunch() {
        assertScreenTitle(R.string.app_name)
    }

    /**
     * Controlla il passaggio Home -> Inventario tramite bottom navigation.
     */
    @Test
    fun shouldNavigateFromHomeToInventory() {
        navigateToPrimarySection(R.string.bottom_navigation_inventory)

        assertScreenTitle(R.string.inventory_screen_title)
    }

    /**
     * Controlla il ritorno Inventario -> Home tramite bottom navigation.
     */
    @Test
    fun shouldNavigateBackToHomeFromInventory() {
        navigateToPrimarySection(R.string.bottom_navigation_inventory)

        assertScreenTitle(R.string.inventory_screen_title)

        navigateToPrimarySection(R.string.bottom_navigation_home)

        assertScreenTitle(R.string.app_name)
    }

    /**
     * Controlla il passaggio Home -> Scadenzario tramite bottom navigation.
     */
    @Test
    fun shouldNavigateFromHomeToExpiry() {
        navigateToPrimarySection(R.string.bottom_navigation_expiry)

        assertScreenTitle(R.string.expiry_screen_title)
    }

    /**
     * Controlla il passaggio Home -> Impostazioni tramite bottom navigation.
     */
    @Test
    fun shouldNavigateFromHomeToSettings() {
        navigateToPrimarySection(R.string.bottom_navigation_settings)

        assertScreenTitle(R.string.settings_screen_title)
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

    /** Il salvataggio dalla Home deve ripristinare la Home. */
    @Test
    fun shouldReturnHomeAfterSavingMedicine() {
        saveMedicineAndAssertOrigin(R.string.app_name)
    }

    /** Il salvataggio dallo Scadenzario deve ripristinare lo Scadenzario. */
    @Test
    fun shouldReturnExpiryAfterSavingMedicine() {
        navigateToPrimarySection(R.string.bottom_navigation_expiry)
        saveMedicineAndAssertOrigin(R.string.expiry_screen_title)
    }

    /** Seleziona il tab interattivo, senza dipendere dalla semantica interna dell'immagine. */
    private fun navigateToPrimarySection(labelRes: Int) {
        composeTestRule.onNode(hasText(composeTestRule.activity.getString(labelRes)) and hasClickAction())
            .performClick()
    }

    /** Distingue il titolo della schermata dal tab che può avere la stessa etichetta. */
    private fun assertScreenTitle(titleRes: Int) {
        composeTestRule.onNode(hasText(composeTestRule.activity.getString(titleRes)) and !hasClickAction())
            .assertIsDisplayed()
    }

    /** Inserisce il solo nome, salva e attende il titolo della destinazione di origine. */
    private fun saveMedicineAndAssertOrigin(titleRes: Int) {
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.navigation_action_add_medicine),
        ).performClick()
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.medicine_form_name_label),
        ).performTextInput("Medicinale test navigazione")
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.medicine_form_save_action),
        ).performScrollTo().performClick()
        val title = composeTestRule.activity.getString(titleRes)
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodes(hasText(title) and !hasClickAction())
                .fetchSemanticsNodes().isNotEmpty()
        }
        assertScreenTitle(titleRes)
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(R.string.medicine_form_save_success),
        ).assertIsDisplayed()
    }
}
