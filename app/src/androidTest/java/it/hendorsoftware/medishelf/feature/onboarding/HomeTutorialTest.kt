package it.hendorsoftware.medishelf.feature.onboarding

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import it.hendorsoftware.medishelf.feature.home.HomeScreen
import it.hendorsoftware.medishelf.feature.home.HomeUiState
import it.hendorsoftware.medishelf.feature.home.HomeAttentionItemUiModel
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import androidx.test.platform.app.InstrumentationRegistry
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verifica ordine, completamento e uscita anticipata dai tooltip. */
@OptIn(ExperimentalTestApi::class)
class HomeTutorialTest {
    @get:Rule val composeRule = createComposeRule()

    /** Le cinque spiegazioni compaiono nell’ordine richiesto e Fine chiude la guida. */
    @Test fun shouldShowFiveStepsAndFinish() {
        var completed by mutableStateOf(false)
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
        composeRule.onNodeWithText(context.getString(R.string.tutorial_navigation_title)).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.tutorial_finish)).assertDoesNotExist()
    }

    /** Il focus iniziale passa dal pulsante sottostante alla finestra modale. */
    @Test fun shouldContainKeyboardFocusInsideTutorial() {
        var visible by mutableStateOf(false)
        var backgroundActions = 0
        var tutorialActions = 0
        composeRule.setContent {
            val backgroundFocus = remember { FocusRequester() }
            val inputMode = LocalInputModeManager.current
            MediShelfTheme {
                Box(Modifier.fillMaxSize()) {
                    Button(onClick = { backgroundActions++ }, modifier = Modifier.focusRequester(backgroundFocus)) {
                        Text("Background")
                    }
                    if (visible) HomeTutorialOverlay(0, emptyMap(), { tutorialActions++ }, { tutorialActions++ })
                }
                LaunchedEffect(Unit) {
                    inputMode.requestInputMode(InputMode.Keyboard)
                    withFrameNanos { }
                    backgroundFocus.requestFocus()
                }
            }
        }
        composeRule.onNodeWithText("Background").assertIsFocused()
        composeRule.runOnIdle { visible = true }
        val message = composeRule.onNodeWithTag(HomeTutorialTestTags.Message)
        // I veri eventi di tastiera attivano anche la modalità Keyboard del Popup.
        repeat(6) {
            message.performKeyInput { pressKey(Key.Tab) }
            composeRule.onNode(isFocused() and hasAnyAncestor(hasTestTag(HomeTutorialTestTags.Message)))
                .assertExists()
        }
        message.performKeyInput {
            keyDown(Key.ShiftLeft)
            pressKey(Key.Tab)
            keyUp(Key.ShiftLeft)
            pressKey(Key.Enter)
        }
        composeRule.runOnIdle {
            assertEquals(0, backgroundActions)
            assertEquals(1, tutorialActions)
        }
    }

    /** I bersagli restano visibili fuori dal messaggio anche in una finestra bassa. */
    @Test fun shouldKeepTargetsClearOfMessageInShortWindow() = checkTargetVisibility(1f, false)

    /** Font grandi e lista popolata mantengono un’area evidenziata libera dal messaggio. */
    @Test fun shouldKeepTargetsClearWithLargeTextAndMedicines() = checkTargetVisibility(1.6f, true)

    private fun checkTargetVisibility(fontScale: Float, withMedicine: Boolean) {
        val step = mutableIntStateOf(0)
        val targets = mutableStateMapOf<TutorialTarget, Rect>()
        val message = mutableStateOf<TutorialMessageArea?>(null)
        val viewport = mutableStateOf<Rect?>(null)
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale),
                LocalTutorialTargets provides targets,
                LocalTutorialStep provides step.intValue,
                LocalTutorialMessageArea provides message,
                LocalTutorialViewportBounds provides viewport,
            ) {
                MediShelfTheme {
                    Box(Modifier.fillMaxWidth().height(320.dp)) {
                        Column(Modifier.fillMaxSize()) {
                            HomeScreen(
                                uiState = HomeUiState(isLoading = false, attentionItems = if (withMedicine) listOf(
                                    HomeAttentionItemUiModel(id = "1", name = "Paracetamolo", packageForm = null, expirationDate = null, quantity = null, status = MedicineStatusBadgeStatus.Expired),
                                ) else emptyList()),
                                onAddMedicineClick = {}, onInventoryClick = {}, onExpiryClick = {},
                                onAttentionItemClick = {}, modifier = Modifier.weight(1f),
                            )
                            Text("Navigazione", Modifier.fillMaxWidth().tutorialTarget(TutorialTarget.Navigation))
                        }
                        HomeTutorialOverlay(step.intValue, targets, { }, { })
                    }
                }
            }
        }
        TutorialTarget.entries.forEach { target ->
            composeRule.runOnIdle { step.intValue = target.ordinal }
            composeRule.waitForIdle()
            composeRule.runOnIdle {
                val bounds = requireNotNull(targets[target])
                val messageBounds = requireNotNull(message.value).bounds
                assertTrue("Bersaglio visibile: $target, $bounds", bounds.width > 0f && bounds.height > 0f)
                assertFalse("Messaggio sovrapposto a $target: $messageBounds / $bounds", messageBounds.overlaps(bounds))
            }
        }
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
