package it.hendorsoftware.medishelf

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import androidx.test.core.app.ActivityScenario
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Test

/** Regressioni sulla scelta delle risorse quando Android combina più lingue. */
class LocaleSelectionTest {
    /** L'inglese secondario non deve prevalere sull'italiano preferito dall'utente. */
    @Test
    fun shouldPreferItalianBeforeEnglishInSystemLocaleList() {
        assertItalianResources(contextFor("it-IT,en-US"))
    }

    /** La preferenza inversa continua a risolvere le risorse inglesi. */
    @Test
    fun shouldPreferEnglishBeforeItalianInSystemLocaleList() {
        val context = contextFor("en-US,it-IT")
        assertEquals("Settings", context.getString(R.string.settings_screen_title))
        assertEquals("tablets", context.resources.getStringArray(R.array.medicine_package_form_labels)[5])
    }

    /** Una lingua non supportata mantiene il fallback italiano completo. */
    @Test
    fun shouldKeepItalianDefaultForUnsupportedLocale() {
        assertItalianResources(contextFor("ja-JP"))
    }

    /** Prova l'override reale per app su sistema inglese e ripristina sempre la scelta precedente. */
    @Test
    @SdkSuppress(minSdkVersion = 33)
    fun shouldUseItalianAppLocaleOnEnglishSystem() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val manager = context.getSystemService(LocaleManager::class.java)
        assumeTrue("Questa regressione richiede un sistema in inglese", manager.systemLocales[0].language == "en")
        val previous = manager.applicationLocales
        try {
            manager.applicationLocales = LocaleList.forLanguageTags("it")
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    assertEquals("it", activity.resources.configuration.locales[0].language)
                    assertItalianResources(activity)
                }
            }
        } finally {
            manager.applicationLocales = previous
        }
    }

    /** Crea una lista locale isolata senza cambiare le lingue del sistema. */
    private fun contextFor(languageTags: String): Context {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val configuration = Configuration(context.resources.configuration).apply {
            setLocales(LocaleList.forLanguageTags(languageTags))
        }
        return context.createConfigurationContext(configuration)
    }

    /** Controlla copy, notifiche, etichette localizzate e chiavi persistenti indipendenti dalla lingua. */
    private fun assertItalianResources(context: Context) {
        assertEquals("Impostazioni", context.getString(R.string.settings_screen_title))
        assertEquals("Benvenuto in MediShelf", context.getString(R.string.onboarding_title))
        assertEquals(
            "In scadenza: 2. Scaduti: 1. Apri MediShelf per consultarli.",
            context.getString(R.string.expiry_notification_summary, 2, 1),
        )
        assertEquals("compresse", context.resources.getStringArray(R.array.medicine_package_form_labels)[5])
        assertEquals("compresse", context.resources.getStringArray(R.array.medicine_package_forms)[5])
    }
}
