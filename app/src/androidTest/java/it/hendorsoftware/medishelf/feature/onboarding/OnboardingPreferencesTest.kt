package it.hendorsoftware.medishelf.feature.onboarding

import android.content.Context
import android.content.ContextWrapper
import androidx.test.platform.app.InstrumentationRegistry
import it.hendorsoftware.medishelf.data.local.preferences.SharedPreferencesUserSettingsLocalDataSource
import it.hendorsoftware.medishelf.data.repository.LocalUserSettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/** Esercita il file SharedPreferences reale in uno spazio isolato dai dati dell'app. */
class OnboardingPreferencesTest {
    /** La chiave assente mostra l'introduzione; la ricreazione rilegge la chiusura persistita. */
    @Test fun shouldRememberCompletionAcrossDataSourceRecreation() = runBlocking {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val isolated = object : ContextWrapper(base) {
            override fun getSharedPreferences(name: String, mode: Int) =
                super.getSharedPreferences("test_onboarding_$name", mode)
        }
        val file = isolated.getSharedPreferences("medishelf_user_settings", Context.MODE_PRIVATE)
        file.edit().clear().commit()
        try {
            val source = SharedPreferencesUserSettingsLocalDataSource(isolated)
            assertFalse(source.readSettings().hasSeenOnboarding)
            LocalUserSettingsRepository(source).completeOnboarding()
            val recreated = SharedPreferencesUserSettingsLocalDataSource(isolated)
            assertTrue(recreated.observeSettings().first().hasSeenOnboarding)
        } finally {
            file.edit().clear().commit()
        }
    }
}
