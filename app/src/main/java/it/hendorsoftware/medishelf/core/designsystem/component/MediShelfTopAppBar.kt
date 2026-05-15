package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Top app bar base per le schermate MediShelf.
 *
 * @param title titolo visibile della schermata.
 * @param modifier modificatore Compose applicato alla barra.
 * @param navigationIcon slot opzionale per l'azione di navigazione.
 * @param actions slot opzionale per azioni contestuali.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediShelfTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

/**
 * Preview della top app bar base.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfTopAppBarPreview() {
    MediShelfTheme {
        MediShelfTopAppBar(
            title = stringResource(R.string.designsystem_top_app_bar_title),
        )
    }
}
