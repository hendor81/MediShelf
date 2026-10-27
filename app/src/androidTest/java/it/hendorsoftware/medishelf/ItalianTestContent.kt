package it.hendorsoftware.medishelf

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.test.platform.app.InstrumentationRegistry
import java.util.Locale

/** Contesto italiano isolato per le regressioni UI che verificano esplicitamente il copy italiano. */
fun italianTestContext(): Context {
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val configuration = Configuration(context.resources.configuration).apply {
        setLocale(Locale.ITALIAN)
    }
    return context.createConfigurationContext(configuration)
}

/** Mantiene deterministici i test del copy italiano anche su emulatori configurati in inglese. */
fun ComposeContentTestRule.setItalianContent(content: @Composable () -> Unit) {
    val context = italianTestContext()
    setContent {
        CompositionLocalProvider(
            LocalContext provides context,
            LocalConfiguration provides context.resources.configuration,
            LocalResources provides context.resources,
            content = content,
        )
    }
}
