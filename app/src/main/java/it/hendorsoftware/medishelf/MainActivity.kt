package it.hendorsoftware.medishelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode

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
            val viewModel: MainViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (uiState.themeMode) {
                SettingsThemeMode.LIGHT -> false
                SettingsThemeMode.DARK -> true
                SettingsThemeMode.SYSTEM -> systemDarkTheme
            }

            MediShelfTheme(darkTheme = darkTheme) {
                MediShelfApp()
            }
        }
    }
}
