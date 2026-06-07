package it.hendorsoftware.medishelf.domain.model

import it.hendorsoftware.medishelf.core.common.MediShelfDefaults

/**
 * Preferenze locali minime della versione Free.
 *
 * @param themeMode modalita tema applicata all'app.
 * @param expiringThresholdDays giorni entro cui una scadenza e considerata vicina.
 * @param notificationsEnabled indica se le notifiche locali sono abilitate.
 * @param preferArchiveOverDelete preferenza tra archiviazione reversibile e cancellazione definitiva.
 *
 * Le impostazioni restano locali e offline-first. Il costruttore valida la soglia
 * per evitare che un valore persistito incoerente entri nei calcoli di dominio.
 */
data class UserSettings(
    val themeMode: SettingsThemeMode = SettingsThemeMode.LIGHT,
    val expiringThresholdDays: Int = MediShelfDefaults.ExpiringThresholdDays,
    val notificationsEnabled: Boolean = MediShelfDefaults.NotificationsEnabled,
    val preferArchiveOverDelete: Boolean = MediShelfDefaults.PreferArchiveOverDelete,
) {
    init {
        require(expiringThresholdDays in MediShelfDefaults.MinExpiringThresholdDays..MediShelfDefaults.MaxExpiringThresholdDays) {
            "Expiring threshold days must be between ${MediShelfDefaults.MinExpiringThresholdDays} and ${MediShelfDefaults.MaxExpiringThresholdDays}."
        }
    }
}
