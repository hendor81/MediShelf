package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/** Pulsante accessibile per tornare alla destinazione precedente. */
@Composable
fun BackNavigationButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.navigation_action_go_back))
    }
}

/** Preview del pulsante di navigazione. */
@Preview
@Composable
private fun BackNavigationButtonPreview() {
    MediShelfTheme { BackNavigationButton {} }
}
