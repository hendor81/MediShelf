package it.hendorsoftware.medishelf

import android.content.pm.PackageManager
import android.Manifest
import androidx.core.content.edit
import it.hendorsoftware.medishelf.core.notification.NotificationPermissionHistory
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Activity principale dell'app.
 *
 * Avvia la navigazione Compose, applica il tema scelto e richiede una sola volta
 * il permesso per le notifiche locali quando la preferenza è attiva.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var settingsRepository: UserSettingsRepository
    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    /**
     * Inizializza la UI Compose dell'app.
     *
     * @param savedInstanceState stato Android eventualmente ripristinato dal sistema.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            val preferences = getSharedPreferences(NotificationPermissionHistory.PREFERENCES_NAME, MODE_PRIVATE)
            // Il primo avvio richiede il permesso una sola volta; nuovi tentativi partono dalle Impostazioni.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                settingsRepository.observeSettings().first { it.hasSeenOnboarding }.notificationsEnabled &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
                !preferences.getBoolean(NotificationPermissionHistory.REQUESTED_KEY, false)
            ) {
                preferences.edit { putBoolean(NotificationPermissionHistory.REQUESTED_KEY, true) }
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

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
                MainContent(uiState, viewModel::onCompleteOnboarding)
            }
        }
    }
}
