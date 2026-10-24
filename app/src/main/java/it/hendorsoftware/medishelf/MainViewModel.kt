package it.hendorsoftware.medishelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.usecase.CompleteOnboardingUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel root dell'app per preferenze che influenzano l'intera UI.
 *
 * @param observeUserSettingsUseCase use case che espone le impostazioni locali.
 * @param completeOnboardingUseCase persiste la chiusura dell'introduzione.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
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
                _uiState.update {
                    it.copy(
                        themeMode = settings.themeMode,
                        isLoading = false,
                        hasSeenOnboarding = settings.hasSeenOnboarding,
                    )
                }
            }
        }
    }

    /** Chiude o salta l'introduzione solo dopo il salvataggio; un errore consente il retry. */
    fun onCompleteOnboarding() {
        if (_uiState.value.isSaving || _uiState.value.hasSeenOnboarding) return
        _uiState.update { it.copy(isSaving = true, hasSaveError = false) }
        viewModelScope.launch {
            try {
                completeOnboardingUseCase()
                _uiState.update { it.copy(isSaving = false, hasSeenOnboarding = true) }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.update { it.copy(isSaving = false, hasSaveError = true) }
            }
        }
    }
}

/**
 * Stato globale minimo necessario prima del grafo di navigazione.
 *
 * @param themeMode modalita tema persistita.
 * @param isLoading attesa della prima lettura delle preferenze.
 * @param hasSeenOnboarding introduzione già chiusa o saltata.
 * @param isSaving salvataggio della chiusura in corso.
 * @param hasSaveError errore recuperabile di salvataggio.
 */
data class MainUiState(
    val isLoading: Boolean = true,
    val hasSeenOnboarding: Boolean = false,
    val isSaving: Boolean = false,
    val hasSaveError: Boolean = false,
    val themeMode: SettingsThemeMode = SettingsThemeMode.LIGHT,
)
