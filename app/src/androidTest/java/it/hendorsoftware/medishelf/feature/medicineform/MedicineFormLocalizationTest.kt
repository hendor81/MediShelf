package it.hendorsoftware.medishelf.feature.medicineform

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.platform.app.InstrumentationRegistry
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.MedicinePackageForms
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Verifica lingua delle risorse e compatibilità dei formati salvati cambiando locale. */
class MedicineFormLocalizationTest {
    @get:Rule
    val compose = createComposeRule()

    /** La selezione inglese emette ancora la chiave italiana usata dai dati esistenti. */
    @Test
    fun shouldDisplayEnglishAndKeepStoredPackageForm() {
        val context = localizedContext("en")
        var selected = ""
        compose.setContent {
            CompositionLocalProvider(
                LocalContext provides context,
                LocalConfiguration provides context.resources.configuration,
                LocalResources provides context.resources,
            ) {
                MediShelfTheme {
                    MedicinePackageFormSelector("Compresse", true) { selected = it }
                }
            }
        }
        compose.onNodeWithText("tablets").assertIsDisplayed().performClick()
        compose.onNodeWithText("ointment").performScrollTo().performClick()
        compose.runOnIdle { assertEquals("pomata", selected) }
    }

    /** Il testo libero inserito dall'utente non viene tradotto o normalizzato. */
    @Test
    fun shouldPreserveCustomPackageFormInEnglish() {
        val context = localizedContext("en")
        compose.setContent {
            CompositionLocalProvider(
                LocalContext provides context,
                LocalConfiguration provides context.resources.configuration,
                LocalResources provides context.resources,
            ) {
                MediShelfTheme { Text(MedicinePackageForms.labelFor("Confezione personale").orEmpty()) }
            }
        }
        compose.onNodeWithText("Confezione personale").assertIsDisplayed()
    }

    /** Controlla entrambe le lingue e gli argomenti dei messaggi esterni alla UI Compose. */
    @Test
    fun shouldLocalizeScreensAndNotifications() {
        val english = localizedContext("en")
        val italian = localizedContext("it")
        assertEquals("Settings", english.getString(R.string.settings_screen_title))
        assertEquals("Impostazioni", italian.getString(R.string.settings_screen_title))
        assertEquals(
            "Expiring soon: 2. Expired: 1. Open MediShelf to review them.",
            english.getString(R.string.expiry_notification_summary, 2, 1),
        )
        assertEquals(
            italian.resources.getStringArray(R.array.medicine_package_forms).toList(),
            english.resources.getStringArray(R.array.medicine_package_forms).toList(),
        )
    }

    /** Crea un contesto isolato senza cambiare la lingua del dispositivo o degli altri test. */
    private fun localizedContext(language: String) =
        InstrumentationRegistry.getInstrumentation().targetContext.let { context ->
            val configuration = Configuration(context.resources.configuration).apply {
                setLocale(Locale.forLanguageTag(language))
            }
            context.createConfigurationContext(configuration)
        }
}
