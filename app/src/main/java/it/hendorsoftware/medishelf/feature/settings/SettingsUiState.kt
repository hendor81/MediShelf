package it.hendorsoftware.medishelf.feature.settings

import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode

/**
 * Stato UI della schermata Impostazioni.
 *
 * @param isLoading indica se le preferenze sono ancora in lettura.
 * @param themeMode tema corrente.
 * @param expiringThresholdDays soglia corrente per lo stato in scadenza.
 * @param notificationsEnabled stato delle notifiche locali.
 * @param preferArchiveOverDelete true quando l'azione predefinita e archiviare.
 * @param isThemeDialogVisible indica se mostrare il dialog tema.
 * @param isThresholdDialogVisible indica se mostrare il dialog soglia.
 * @param thresholdInput testo temporaneo del campo soglia.
 * @param thresholdInputError errore di validazione del campo soglia.
 * @param isRemovalPreferenceDialogVisible indica se mostrare il dialog azione predefinita.
 * @param isClearArchiveDialogVisible indica se mostrare il dialog di svuotamento archivio.
 * @param isPrivacyDialogVisible indica se mostrare privacy e disclaimer.
 * @param isActionInProgress indica se una modifica e in corso.
 * @param feedback feedback one-shot da mostrare nella UI.
 */
data class SettingsUiState(
    val isLoading: Boolean = true,
    val themeMode: SettingsThemeMode = SettingsThemeMode.LIGHT,
    val expiringThresholdDays: Int = MediShelfDefaults.ExpiringThresholdDays,
    val notificationsEnabled: Boolean = MediShelfDefaults.NotificationsEnabled,
    val preferArchiveOverDelete: Boolean = MediShelfDefaults.PreferArchiveOverDelete,
    val isThemeDialogVisible: Boolean = false,
    val isThresholdDialogVisible: Boolean = false,
    val thresholdInput: String = "",
    val thresholdInputError: SettingsThresholdInputError? = null,
    val isRemovalPreferenceDialogVisible: Boolean = false,
    val isClearArchiveDialogVisible: Boolean = false,
    val isPrivacyDialogVisible: Boolean = false,
    val isActionInProgress: Boolean = false,
    val feedback: SettingsFeedback? = null,
)

/**
 * Errori possibili nel campo numerico della soglia.
 */
enum class SettingsThresholdInputError {
    BLANK,
    INVALID_NUMBER,
    OUT_OF_RANGE,
}

/**
 * Feedback prodotto dalle azioni della schermata.
 */
enum class SettingsFeedback {
    ARCHIVE_EMPTY,
    ARCHIVE_CLEARED,
}
