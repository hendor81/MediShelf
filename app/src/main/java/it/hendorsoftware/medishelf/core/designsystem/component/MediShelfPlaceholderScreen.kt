package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Layout condiviso per le schermate placeholder della navigation shell.
 *
 * @param title titolo principale del contenuto.
 * @param body testo descrittivo del contenuto futuro della schermata.
 * @param modifier modificatore Compose applicato al contenitore.
 * @param actions slot opzionale per azioni di navigazione o conferma.
 */
@Composable
fun MediShelfPlaceholderScreen(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            actions()
        }
    }
}

/**
 * Preview del layout placeholder condiviso.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfPlaceholderScreenPreview() {
    MediShelfTheme {
        MediShelfPlaceholderScreen(
            title = stringResource(R.string.designsystem_empty_state_title),
            body = stringResource(R.string.designsystem_empty_state_body),
        )
    }
}
