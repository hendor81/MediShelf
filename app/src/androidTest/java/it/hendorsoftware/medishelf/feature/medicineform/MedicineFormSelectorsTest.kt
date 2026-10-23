package it.hendorsoftware.medishelf.feature.medicineform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verifica la selezione del formato e la rimozione della scadenza dal calendario. */
class MedicineFormSelectorsTest {
    @get:Rule
    val compose = createComposeRule()

    /** La scelta del menu aggiorna il formato che verrà salvato. */
    @Test
    fun shouldSelectPackageForm() {
        var selected = ""
        compose.setContent {
            var value by remember { mutableStateOf("") }
            MediShelfTheme {
                MedicinePackageFormSelector(value, true) { value = it; selected = it }
            }
        }
        compose.onNodeWithText("Formato o confezione").performClick()
        compose.onNodeWithText("pomata").performClick()
        compose.runOnIdle { assertEquals("pomata", selected) }
    }

    /** La data rimane opzionale anche dopo averla selezionata. */
    @Test
    fun shouldClearExpirationFromCalendar() {
        var selected = "2026-12-31"
        compose.setContent {
            MediShelfTheme { MedicineExpirationSelector(selected, true) { selected = it } }
        }
        compose.onNodeWithContentDescription("Data scadenza").performClick()
        compose.onNodeWithText("Rimuovi data").performClick()
        compose.runOnIdle { assertEquals("", selected) }
    }
}
