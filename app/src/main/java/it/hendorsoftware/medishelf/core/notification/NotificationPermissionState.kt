package it.hendorsoftware.medishelf.core.notification

/** Fotografia dei permessi Android, separata dal framework per verificare il flusso di recupero. */
data class NotificationPermissionState(
    val runtimePermissionRequired: Boolean,
    val runtimePermissionGranted: Boolean,
    val notificationsAllowed: Boolean,
    val requestedBefore: Boolean,
    val shouldShowRationale: Boolean,
) {
    /** Consente il salvataggio della preferenza solo quando Android permette le notifiche. */
    val canEnable: Boolean
        get() = notificationsAllowed && (!runtimePermissionRequired || runtimePermissionGranted)

    /** Decide se abilitare, richiedere, spiegare il rifiuto o proporre le impostazioni Android. */
    fun enableAction(): NotificationPermissionAction = when {
        canEnable -> NotificationPermissionAction.ENABLE
        !runtimePermissionRequired || runtimePermissionGranted -> NotificationPermissionAction.OPEN_SETTINGS
        shouldShowRationale -> NotificationPermissionAction.EXPLAIN_REQUEST
        requestedBefore -> NotificationPermissionAction.OPEN_SETTINGS
        else -> NotificationPermissionAction.REQUEST
    }
}

/** Azioni del flusso di abilitazione esplicita delle notifiche. */
enum class NotificationPermissionAction {
    ENABLE, REQUEST, EXPLAIN_REQUEST, OPEN_SETTINGS,
}

/** Chiavi condivise tra primo avvio e richiesta esplicita dalle Impostazioni. */
object NotificationPermissionHistory {
    const val PREFERENCES_NAME = "notification_permission"
    const val REQUESTED_KEY = "requested"
}
