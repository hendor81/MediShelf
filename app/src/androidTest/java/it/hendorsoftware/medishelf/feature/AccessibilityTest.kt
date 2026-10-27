package it.hendorsoftware.medishelf.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.*
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.feature.home.*
import it.hendorsoftware.medishelf.feature.medicinedetail.*
import it.hendorsoftware.medishelf.feature.settings.*
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import org.junit.Assert.*
import it.hendorsoftware.medishelf.setItalianContent
import org.junit.Rule
import org.junit.Test

/** Regressioni su semantica, target tattili e font al 200% nei flussi principali. */
class AccessibilityTest {
    @get:Rule val compose = createComposeRule()

    /** Edit e delete restano riconoscibili e hanno target di almeno 48 dp. */
    @Test fun shouldExposeAccessibleDetailActions() {
        compose.setItalianContent {
            MediShelfTheme {
                MedicineDetailScreen(
                    MedicineDetailUiState(isLoading = false, medicine = MedicineDetailUiModel(
                        "1", "Paracetamolo", null, MedicineStatusBadgeStatus.Valid,
                        quantity = null, isQuantityAtZero = false, expirationDate = null,
                        storageLocation = null, notes = null, isArchived = false,
                    )),
                    {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
                )
            }
        }
        listOf("Modifica medicinale", "Elimina medicinale").forEach { description ->
            compose.onNodeWithContentDescription(description)
                .assertHasClickAction().assertTouchWidthIsEqualTo(48.dp).assertTouchHeightIsEqualTo(48.dp)
        }
    }

    /** Ogni stato è annunciato e visibile anche senza distinguere i colori. */
    @Test fun shouldExposeTextForEveryStatus() {
        compose.setItalianContent {
            MediShelfTheme {
                Column { MedicineStatusBadgeStatus.entries.forEach { MedicineStatusBadge(it) } }
            }
        }
        listOf("Valido", "In scadenza", "Scaduto", "Scorta bassa", "Esaurito", "Senza data", "Archiviato")
            .forEach { compose.onNodeWithText(it).assertExists() }
    }

    /** Le card si dispongono su due righe e le etichette non vengono tagliate. */
    @Test fun shouldKeepHomeLabelsReadableAtLargeFont() {
        compose.setItalianContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                MediShelfTheme {
                    HomeScreen(HomeUiState(isLoading = false), {}, {}, {}, {}, Modifier.width(320.dp))
                }
            }
        }
        val active = compose.onNodeWithTag(HomeTestTags.summaryCard(R.string.home_summary_active)).fetchSemanticsNode().positionInRoot
        val expired = compose.onNodeWithTag(HomeTestTags.summaryCard(R.string.home_summary_expired)).fetchSemanticsNode().positionInRoot
        assertTrue(expired.y > active.y)
        listOf("Attivi", "In scadenza", "Scaduti", "Scorta bassa").forEach { label ->
            assertTextNotClipped(compose.onNodeWithText(label, useUnmergedTree = true)
                .performScrollTo().assertIsDisplayed())
        }
        compose.onNodeWithTag(HomeTestTags.SeeAllAttention).performScrollTo()
            .assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).assertHasClickAction()
    }

    /** Il nome completo del medicinale resta leggibile con badge e font grandi. */
    @Test fun shouldKeepMedicineNameReadableAtLargeFont() {
        val name = "Paracetamolo 500 mg compresse effervescenti"
        compose.setItalianContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 2f)) {
                MediShelfTheme {
                    MedicineListCard(name, MedicineStatusBadgeStatus.ExpiringSoon, emptyList(), {},
                        modifier = Modifier.width(320.dp), badgeBesideName = true)
                }
            }
        }
        assertTextNotClipped(compose.onNodeWithText(name, useUnmergedTree = true))
        compose.onNodeWithText("In scadenza").assertExists()
    }

    /** Tutta la riga notifiche è un solo switch accessibile con etichetta e stato. */
    @Test fun shouldToggleNotificationsFromLabeledRow() {
        var checked: Boolean? = null
        settingsContent(onNotificationsChanged = { checked = it })
        compose.onNodeWithTag(SettingsTestTags.NotificationsSwitch).performScrollTo()
            .assertIsOn().assertHeightIsAtLeast(48.dp).performClick()
        assertEquals(false, checked)
    }

    /** Le opzioni del tema espongono ruolo radio e selezione, anche senza colore. */
    @Test fun shouldExposeThemeSelection() {
        var selected: SettingsThemeMode? = null
        settingsContent(SettingsUiState(isLoading = false, isThemeDialogVisible = true), onThemeSelected = { selected = it })
        val radio = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
        compose.onNode(radio and hasText("Chiaro")).assertIsSelected()
        compose.onNode(radio and hasText("Scuro")).performClick()
        assertEquals(SettingsThemeMode.DARK, selected)
    }

    /** Il layout del testo deve contenere tutte le righe senza ellissi o overflow. */
    private fun assertTextNotClipped(node: SemanticsNodeInteraction) {
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        assertTrue(layouts.isNotEmpty())
        val layout = layouts.single()
        assertFalse("Testo '${layout.layoutInput.text}' con overflow: ${layout.size}, " +
            "larghezza=${layout.didOverflowWidth}, altezza=${layout.didOverflowHeight}, " +
            "paragrafo=${layout.multiParagraph.width}, " +
            "righe=${(0 until layout.lineCount).map { layout.getLineLeft(it) to layout.getLineRight(it) }}",
            layout.hasVisualOverflow)
    }

    /** Monta la schermata stateless per verificare i controlli senza Hilt. */
    private fun settingsContent(
        state: SettingsUiState = SettingsUiState(isLoading = false),
        onNotificationsChanged: (Boolean) -> Unit = {},
        onThemeSelected: (SettingsThemeMode) -> Unit = {},
    ) {
        compose.setItalianContent {
            MediShelfTheme {
                SettingsScreen(state, {}, onThemeSelected, {}, {}, {}, {}, {}, onNotificationsChanged,
                    {}, {}, {}, {}, {}, {}, {}, {}, {})
            }
        }
    }
}
