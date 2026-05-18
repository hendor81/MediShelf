package it.hendorsoftware.medishelf.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Regola JUnit per sostituire Dispatchers.Main nei test unitari.
 *
 * @param testDispatcher dispatcher controllato dal test.
 *
 * Va riusata nei test di ViewModel o di componenti che dipendono dal Main
 * dispatcher, evitando dipendenze dal Looper Android nei test JVM locali.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    /**
     * Installa il dispatcher di test prima dell'esecuzione del singolo test.
     *
     * @param description descrizione JUnit del test in esecuzione.
     */
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Ripristina Dispatchers.Main dopo il test per non contaminare altri casi.
     *
     * @param description descrizione JUnit del test appena concluso.
     */
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
