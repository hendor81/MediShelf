package it.hendorsoftware.medishelf.feature.settings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.core.notification.NotificationPermissionAction
import it.hendorsoftware.medishelf.core.notification.NotificationPermissionHistory
import it.hendorsoftware.medishelf.core.notification.NotificationPermissionState

/**
 * Collega il toggle ai permessi Android. Mantiene l'intento di abilitazione durante
 * il passaggio alle impostazioni di sistema e ne rilegge l'esito al ritorno.
 * Wrapper tecnico: la UI del dialog è verificabile separatamente in Preview.
 */
@Composable
fun rememberNotificationEnableAction(onEnabledChanged: (Boolean) -> Unit): (Boolean) -> Unit {
    val activity = checkNotNull(LocalActivity.current)
    val preferences = remember(activity) {
        activity.getSharedPreferences(NotificationPermissionHistory.PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    var prompt by rememberSaveable { mutableStateOf<NotificationPermissionAction?>(null) }
    var awaitingSettings by rememberSaveable { mutableStateOf(false) }

    fun permissionState(): NotificationPermissionState {
        val runtimeRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        return NotificationPermissionState(
            runtimePermissionRequired = runtimeRequired,
            runtimePermissionGranted = !runtimeRequired ||
                activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED,
            notificationsAllowed = NotificationManagerCompat.from(activity).areNotificationsEnabled(),
            requestedBefore = preferences.getBoolean(NotificationPermissionHistory.REQUESTED_KEY, false),
            shouldShowRationale = runtimeRequired &&
                activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS),
        )
    }

    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (awaitingSettings) {
            awaitingSettings = false
            // Il resultCode delle Impostazioni non indica il permesso: occorre rileggerlo.
            val allowed = permissionState().canEnable
            onEnabledChanged(allowed)
            prompt = if (allowed) null else NotificationPermissionAction.OPEN_SETTINGS
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        val state = permissionState()
        onEnabledChanged(state.canEnable)
        prompt = if (state.canEnable) null else state.enableAction()
    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            preferences.edit { putBoolean(NotificationPermissionHistory.REQUESTED_KEY, true) }
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (prompt != null) {
        NotificationPermissionDialog(
            blocked = prompt == NotificationPermissionAction.OPEN_SETTINGS,
            onDismiss = { prompt = null },
            onConfirm = {
                val openSettings = prompt == NotificationPermissionAction.OPEN_SETTINGS
                prompt = null
                if (openSettings) {
                    awaitingSettings = true
                    // Android 7 non espone ancora la pagina notifiche tramite questo intent pubblico.
                    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                    } else {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.packageName, null))
                    }
                    settingsLauncher.launch(intent)
                } else {
                    requestPermission()
                }
            },
        )
    }

    return { enabled ->
        if (!enabled) {
            awaitingSettings = false
            prompt = null
            onEnabledChanged(false)
        } else {
            when (val action = permissionState().enableAction()) {
                NotificationPermissionAction.ENABLE -> onEnabledChanged(true)
                NotificationPermissionAction.REQUEST -> requestPermission()
                else -> prompt = action
            }
        }
    }
}

/** Spiega il rifiuto e offre un nuovo tentativo o l'accesso esplicito alle impostazioni Android. */
@Composable
internal fun NotificationPermissionDialog(blocked: Boolean, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.notification_permission_title)) },
        text = { Text(stringResource(if (blocked) R.string.notification_permission_blocked else R.string.notification_permission_rationale)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(if (blocked) R.string.notification_permission_open_settings else R.string.notification_permission_retry))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.notification_permission_cancel)) } },
    )
}

/** Preview del recupero da permesso bloccato. */
@Preview(showBackground = true)
@Composable
private fun NotificationPermissionBlockedPreview() {
    MediShelfTheme { NotificationPermissionDialog(blocked = true, onConfirm = {}, onDismiss = {}) }
}

/** Preview della spiegazione dopo un rifiuto temporaneo. */
@Preview(showBackground = true)
@Composable
private fun NotificationPermissionRationalePreview() {
    MediShelfTheme { NotificationPermissionDialog(blocked = false, onConfirm = {}, onDismiss = {}) }
}
