package it.hendorsoftware.medishelf.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode
import it.hendorsoftware.medishelf.domain.usecase.ClearArchiveUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateExpiringThresholdDaysUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateNotificationsEnabledUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdatePreferArchiveOverDeleteUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateThemeModeUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel delle Impostazioni Free.
 *
 * @param observeUserSettingsUseCase osserva le preferenze locali.
 * @param updateThemeModeUseCase aggiorna il tema.
 * @param updateExpiringThresholdDaysUseCase aggiorna la soglia di scadenza.
 * @param updateNotificationsEnabledUseCase aggiorna lo stato notifiche.
 * @param updatePreferArchiveOverDeleteUseCase aggiorna la preferenza di rimozione.
 * @param clearArchiveUseCase elimina definitivamente le voci archiviate confermate.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val updateThemeModeUseCase: UpdateThemeModeUseCase,
    private val updateExpiringThresholdDaysUseCase: UpdateExpiringThresholdDaysUseCase,
    private val updateNotificationsEnabledUseCase: UpdateNotificationsEnabledUseCase,
    private val updatePreferArchiveOverDeleteUseCase: UpdatePreferArchiveOverDeleteUseCase,
    private val clearArchiveUseCase: ClearArchiveUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente delle Impostazioni.
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            observeUserSettingsUseCase().collect { settings ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        themeMode = settings.themeMode,
                        expiringThresholdDays = settings.expiringThresholdDays,
                        notificationsEnabled = settings.notificationsEnabled,
                        preferArchiveOverDelete = settings.preferArchiveOverDelete,
                    )
                }
            }
        }
    }

    /**
     * Mostra il dialog di scelta tema.
     */
    fun onThemeClick() {
        _uiState.update { state -> state.copy(isThemeDialogVisible = true) }
    }

    /**
     * Nasconde il dialog tema.
     */
    fun onThemeDialogDismissed() {
        _uiState.update { state -> state.copy(isThemeDialogVisible = false) }
    }

    /**
     * Salva il tema selezionato.
     *
     * @param themeMode nuova modalita tema.
     */
    fun onThemeSelected(themeMode: SettingsThemeMode) {
        viewModelScope.launch {
            updateThemeModeUseCase(themeMode)
            _uiState.update { state -> state.copy(isThemeDialogVisible = false) }
        }
    }

    /**
     * Mostra il dialog per modificare la soglia di scadenza.
     */
    fun onThresholdClick() {
        _uiState.update { state ->
            state.copy(
                isThresholdDialogVisible = true,
                thresholdInput = state.expiringThresholdDays.toString(),
                thresholdInputError = null,
            )
        }
    }

    /**
     * Aggiorna il testo temporaneo del campo soglia.
     *
     * @param value nuovo testo digitato.
     */
    fun onThresholdInputChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                thresholdInput = value,
                thresholdInputError = null,
            )
        }
    }

    /**
     * Nasconde il dialog soglia senza salvare.
     */
    fun onThresholdDialogDismissed() {
        _uiState.update { state ->
            state.copy(
                isThresholdDialogVisible = false,
                thresholdInputError = null,
            )
        }
    }

    /**
     * Valida e salva la nuova soglia.
     */
    fun onThresholdSaveClick() {
        val input = _uiState.value.thresholdInput.trim()
        val parsedValue = input.toIntOrNull()
        val error = when {
            input.isBlank() -> SettingsThresholdInputError.BLANK
            parsedValue == null -> SettingsThresholdInputError.INVALID_NUMBER
            parsedValue !in MediShelfDefaults.MinExpiringThresholdDays..MediShelfDefaults.MaxExpiringThresholdDays ->
                SettingsThresholdInputError.OUT_OF_RANGE
            else -> null
        }

        if (error != null || parsedValue == null) {
            _uiState.update { state -> state.copy(thresholdInputError = error) }
            return
        }

        viewModelScope.launch {
            updateExpiringThresholdDaysUseCase(parsedValue)
            _uiState.update { state ->
                state.copy(
                    isThresholdDialogVisible = false,
                    thresholdInputError = null,
                )
            }
        }
    }

    /**
     * Aggiorna l'abilitazione delle notifiche locali.
     *
     * @param enabled true quando l'utente abilita le notifiche.
     */
    fun onNotificationsEnabledChanged(enabled: Boolean) {
        viewModelScope.launch {
            updateNotificationsEnabledUseCase(enabled)
        }
    }

    /**
     * Mostra il dialog per scegliere l'azione predefinita di rimozione.
     */
    fun onRemovalPreferenceClick() {
        _uiState.update { state -> state.copy(isRemovalPreferenceDialogVisible = true) }
    }

    /**
     * Nasconde il dialog dell'azione predefinita.
     */
    fun onRemovalPreferenceDialogDismissed() {
        _uiState.update { state -> state.copy(isRemovalPreferenceDialogVisible = false) }
    }

    /**
     * Salva l'azione predefinita.
     *
     * @param preferArchiveOverDelete true per archiviare, false per eliminare.
     */
    fun onRemovalPreferenceSelected(preferArchiveOverDelete: Boolean) {
        viewModelScope.launch {
            updatePreferArchiveOverDeleteUseCase(preferArchiveOverDelete)
            _uiState.update { state -> state.copy(isRemovalPreferenceDialogVisible = false) }
        }
    }

    /**
     * Mostra il dialog di conferma per svuotare l'archivio.
     */
    fun onClearArchiveClick() {
        _uiState.update { state -> state.copy(isClearArchiveDialogVisible = true) }
    }

    /**
     * Nasconde il dialog di svuotamento archivio.
     */
    fun onClearArchiveDismissed() {
        _uiState.update { state -> state.copy(isClearArchiveDialogVisible = false) }
    }

    /**
     * Cancella definitivamente i medicinali archiviati dopo conferma.
     */
    fun onClearArchiveConfirmed() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isActionInProgress = true,
                    isClearArchiveDialogVisible = false,
                    feedback = null,
                )
            }
            val deletedCount = clearArchiveUseCase()
            _uiState.update { state ->
                state.copy(
                    isActionInProgress = false,
                    feedback = if (deletedCount == 0) {
                        SettingsFeedback.ARCHIVE_EMPTY
                    } else {
                        SettingsFeedback.ARCHIVE_CLEARED
                    },
                )
            }
        }
    }

    /**
     * Mostra privacy e disclaimer.
     */
    fun onPrivacyClick() {
        _uiState.update { state -> state.copy(isPrivacyDialogVisible = true) }
    }

    /**
     * Nasconde privacy e disclaimer.
     */
    fun onPrivacyDialogDismissed() {
        _uiState.update { state -> state.copy(isPrivacyDialogVisible = false) }
    }

    /**
     * Consuma il feedback dopo la visualizzazione.
     */
    fun onFeedbackShown() {
        _uiState.update { state -> state.copy(feedback = null) }
    }
}
