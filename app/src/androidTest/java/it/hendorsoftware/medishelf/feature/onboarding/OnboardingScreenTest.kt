package it.hendorsoftware.medishelf.feature.onboarding

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import it.hendorsoftware.medishelf.MainUiState
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verifica le due uscite esplicite dall'introduzione. */
class OnboardingScreenTest {
    @get:Rule val composeRule = createComposeRule()

    /** Inizia inoltra il completamento al chiamante. */
    @Test fun shouldCompleteOnStart() = checkAction(R.string.onboarding_start)

    /** Salta invoca solo la callback che esclude il tutorial. */
    @Test fun shouldCompleteOnSkip() = checkAction(R.string.onboarding_skip)

    private fun checkAction(action: Int) {
        var starts = 0
        var skips = 0
        composeRule.setContent {
            MediShelfTheme { OnboardingScreen(MainUiState(isLoading = false), onComplete = { starts++ }, onSkip = { skips++ }) }
        }
        val text = InstrumentationRegistry.getInstrumentation().targetContext.getString(action)
        composeRule.onNodeWithText(text).performScrollTo().performClick()
        composeRule.runOnIdle { assertEquals(if (action == R.string.onboarding_start) 1 else 0, starts)
            assertEquals(if (action == R.string.onboarding_skip) 1 else 0, skips) }
    }
}
