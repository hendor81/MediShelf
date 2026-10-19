package it.hendorsoftware.medishelf.core.notification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Verifica primo tentativo, rifiuti e lettura dei permessi al ritorno dalle impostazioni. */
class NotificationPermissionStateTest {
    private val denied = NotificationPermissionState(true, false, false, false, false)

    /** Il primo tentativo non viene confuso con un permesso bloccato. */
    @Test fun shouldRequestPermissionOnFirstAttempt() {
        assertEquals(NotificationPermissionAction.REQUEST, denied.enableAction())
    }

    /** Un rifiuto temporaneo richiede una spiegazione prima di riprovare. */
    @Test fun shouldExplainTemporaryDenial() {
        assertEquals(NotificationPermissionAction.EXPLAIN_REQUEST,
            denied.copy(requestedBefore = true, shouldShowRationale = true).enableAction())
    }

    /** Il blocco definitivo offre un percorso di recupero nelle impostazioni Android. */
    @Test fun shouldOfferSettingsWhenPermissionIsBlocked() {
        assertEquals(NotificationPermissionAction.OPEN_SETTINGS,
            denied.copy(requestedBefore = true).enableAction())
    }

    /** Concessione nelle impostazioni: l'intento di abilitazione può essere completato. */
    @Test fun shouldEnableAfterPermissionGrantedInSettings() {
        val returned = denied.copy(requestedBefore = true, runtimePermissionGranted = true, notificationsAllowed = true)
        assertTrue(returned.canEnable)
        assertEquals(NotificationPermissionAction.ENABLE, returned.enableAction())
    }

    /** Ritorno senza concessione: non si salva una preferenza di abilitazione. */
    @Test fun shouldStayDisabledAfterReturningWithoutGrant() {
        assertFalse(denied.copy(requestedBefore = true).canEnable)
    }

    /** Anche prima di Android 13 il blocco globale richiede le impostazioni. */
    @Test fun shouldRecoverGlobalBlockOnOlderAndroid() {
        assertEquals(NotificationPermissionAction.OPEN_SETTINGS,
            denied.copy(runtimePermissionRequired = false).enableAction())
    }

    /** Il solo permesso runtime non basta se il sistema blocca tutte le notifiche. */
    @Test fun shouldNotEnableWhenNotificationsRemainBlocked() {
        val returned = denied.copy(runtimePermissionGranted = true)
        assertFalse(returned.canEnable)
        assertEquals(NotificationPermissionAction.OPEN_SETTINGS, returned.enableAction())
    }

    /** Su versioni precedenti il permesso runtime non è necessario. */
    @Test fun shouldEnableWithoutRuntimePermissionOnOlderAndroid() {
        assertTrue(denied.copy(runtimePermissionRequired = false, notificationsAllowed = true).canEnable)
    }
}
