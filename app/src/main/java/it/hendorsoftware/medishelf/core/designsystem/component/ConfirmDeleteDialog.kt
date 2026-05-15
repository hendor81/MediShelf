package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Dialog riusabile per confermare una cancellazione definitiva.
 *
 * @param title titolo del dialog.
 * @param body testo che spiega cosa accadra' dopo la conferma.
 * @param confirmLabel testo dell'azione distruttiva.
 * @param dismissLabel testo dell'azione di annullamento.
 * @param onConfirm callback invocata solo dopo conferma esplicita.
 * @param onDismiss callback invocata quando il dialog viene chiuso o annullato.
 * @param modifier modificatore Compose applicato al dialog.
 */
@Composable
fun ConfirmDeleteDialog(
    title: String,
    body: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = title)
        },
        text = {
            Text(text = body)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError,
                ),
            ) {
                Text(text = confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissLabel)
            }
        },
    )
}

/**
 * Preview del dialog di conferma cancellazione.
 */
@Preview(showBackground = true)
@Composable
private fun ConfirmDeleteDialogPreview() {
    MediShelfTheme {
        ConfirmDeleteDialog(
            title = stringResource(R.string.designsystem_delete_dialog_title),
            body = stringResource(R.string.designsystem_delete_dialog_body),
            confirmLabel = stringResource(R.string.designsystem_delete_dialog_confirm),
            dismissLabel = stringResource(R.string.designsystem_delete_dialog_dismiss),
            onConfirm = {},
            onDismiss = {},
        )
    }
}
