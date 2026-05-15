package it.hendorsoftware.medishelf.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = SageOnPrimary,
    secondary = OliveSecondary,
    background = WarmBackground,
    surface = WarmSurface,
    surfaceVariant = SoftSurfaceVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftSurfaceVariant,
    onPrimary = DarkBackground,
    secondary = OliveSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
)

/**
 * Tema Compose principale di MediShelf.
 *
 * @param darkTheme indica se usare lo schema colori scuro.
 * @param content contenuto Compose a cui applicare colori, tipografia e shape.
 */
@Composable
fun MediShelfTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = MediShelfTypography,
        shapes = MediShelfShapes,
        content = content,
    )
}
