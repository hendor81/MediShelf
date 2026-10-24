package it.hendorsoftware.medishelf.feature.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verifica ordine, completamento e uscita anticipata dai tooltip. */
class HomeTutorialTest {
    @get:Rule val composeRule = createComposeRule()

    /** Le cinque spiegazioni compaiono nell’ordine richiesto e Fine chiude la guida. */
    @Test fun shouldShowFiveStepsAndFinish() {
        var completed = false
        composeRule.setContent {
            var step by remember { mutableIntStateOf(0) }
            MediShelfTheme {
                if (!completed) HomeTutorialOverlay(step, emptyMap(), {
                    if (step == 4) completed = true else step++
                }, {})
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        TutorialTarget.entries.forEachIndexed { index, target ->
            composeRule.onNodeWithText(context.getString(target.title)).assertIsDisplayed()
            val action = if (index == 4) R.string.tutorial_finish else R.string.onboarding_start
            composeRule.onNodeWithText(context.getString(action)).performScrollTo().performClick()
        }
        composeRule.runOnIdle { assertEquals(true, completed) }
    }

    /** Salta chiude anche da un passo intermedio senza avanzare. */
    @Test fun shouldDismissWithoutAdvancing() {
        var advances = 0
        var dismissals = 0
        composeRule.setContent {
            MediShelfTheme { HomeTutorialOverlay(2, emptyMap(), { advances++ }, { dismissals++ }) }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        composeRule.onNodeWithText(context.getString(R.string.onboarding_skip)).performScrollTo().performClick()
        composeRule.runOnIdle {
            assertEquals(0, advances)
            assertEquals(1, dismissals)
        }
    }
}
