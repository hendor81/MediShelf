package it.hendorsoftware.medishelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Activity principale dell'app.
 *
 * Avvia il contenuto Compose e applica il tema base di MediShelf. La schermata
 * mostrata e' un placeholder controllato dallo scaffold. L'annotazione Hilt
 * abilita l'integrazione futura con ViewModel iniettati nelle route Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Inizializza la UI Compose dell'app.
     *
     * @param savedInstanceState stato Android eventualmente ripristinato dal sistema.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MediShelfTheme {
                MediShelfApp()
            }
        }
    }
}
