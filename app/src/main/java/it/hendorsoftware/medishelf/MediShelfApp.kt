package it.hendorsoftware.medishelf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.navigation.MediShelfNavHost

/**
 * Root composable dell'app MediShelf.
 *
 * Ospita il grafo Navigation Compose principale della versione Free.
 */
@Composable
fun MediShelfApp() {
    MediShelfNavHost(modifier = Modifier.fillMaxSize())
}

/**
 * Preview dello scaffold iniziale MediShelf.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfAppPreview() {
    MediShelfTheme {
        MediShelfApp()
    }
}
