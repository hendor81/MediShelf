package it.hendorsoftware.medishelf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test strumentale minimale generato con lo scaffold.
 *
 * Verifica che il package installato corrisponda all'applicationId richiesto.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    /**
     * Controlla il package dell'app sotto test.
     */
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("it.hendorsoftware.medishelf", appContext.packageName)
    }
}
