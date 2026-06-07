package it.hendorsoftware.medishelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel root dell'app per preferenze che influenzano l'intera UI.
 *
 * @param observeUserSettingsUseCase use case che espone le impostazioni locali.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())

    /**
     * Stato osservabile dal root Compose.
     *
     * @return stream dello stato globale minimo.
     */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeUserSettingsUseCase().collect { settings ->
                _uiState.value = MainUiState(themeMode = settings.themeMode)
            }
        }
    }
}

/**
 * Stato globale minimo necessario prima del grafo di navigazione.
 *
 * @param themeMode modalita tema persistita.
 */
data class MainUiState(
    val themeMode: SettingsThemeMode = SettingsThemeMode.LIGHT,
)
