package it.hendorsoftware.medishelf.core.common

/**
 * Valori di default condivisi dalla versione Free dell'app.
 */
object MediShelfDefaults {

    /**
     * Giorni entro cui una scadenza viene considerata vicina.
     */
    const val ExpiringThresholdDays = 60

    /**
     * Limite minimo consentito per la soglia di scadenza.
     */
    const val MinExpiringThresholdDays = 0

    /**
     * Limite massimo consentito per la soglia di scadenza.
     */
    const val MaxExpiringThresholdDays = 365

    /**
     * Stato iniziale delle notifiche locali.
     */
    const val NotificationsEnabled = true

    /**
     * Preferenza iniziale per rimuovere medicinali dall'inventario attivo.
     */
    const val PreferArchiveOverDelete = true

}
