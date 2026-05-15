package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Stato vuoto riusabile per schermate e sezioni senza contenuti.
 *
 * @param title titolo breve dello stato vuoto.
 * @param body testo di supporto con tono pratico e rassicurante.
 * @param modifier modificatore Compose applicato al contenitore.
 * @param actionLabel testo del pulsante opzionale.
 * @param onActionClick callback del pulsante opzionale.
 * @param icon contenuto visuale opzionale, fornito dal chiamante.
 */
@Composable
fun EmptyState(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(MediShelfDimens.SpacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
    ) {
        if (icon != null) {
            Surface(
                modifier = Modifier.size(MediShelfDimens.EmptyStateIconSize),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                content = icon,
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        if (actionLabel != null && onActionClick != null) {
            Button(onClick = onActionClick) {
                Text(text = actionLabel)
            }
        }
    }
}

/**
 * Preview dello stato vuoto con azione primaria.
 */
@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    MediShelfTheme {
        EmptyState(
            title = stringResource(R.string.designsystem_empty_state_title),
            body = stringResource(R.string.designsystem_empty_state_body),
            actionLabel = stringResource(R.string.designsystem_empty_state_action),
            onActionClick = {},
        )
    }
}
