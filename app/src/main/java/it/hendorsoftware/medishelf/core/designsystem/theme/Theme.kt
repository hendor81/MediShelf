package it.hendorsoftware.medishelf.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = SageOnPrimary,
    primaryContainer = SagePrimaryContainer,
    onPrimaryContainer = SageOnPrimaryContainer,
    secondary = OliveSecondary,
    onSecondary = OliveOnSecondary,
    secondaryContainer = OliveSecondaryContainer,
    onSecondaryContainer = OliveOnSecondaryContainer,
    background = WarmBackground,
    onBackground = WarmOnBackground,
    surface = WarmSurface,
    onSurface = WarmOnSurface,
    surfaceVariant = SoftSurfaceVariant,
    onSurfaceVariant = SoftOnSurfaceVariant,
    outline = CalmOutline,
    error = CalmError,
    onError = CalmOnError,
    errorContainer = CalmErrorContainer,
    onErrorContainer = CalmOnErrorContainer,
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftSurfaceVariant,
    onPrimary = DarkBackground,
    secondary = OliveSecondary,
    onSecondary = SageOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = CalmErrorContainer,
    onError = CalmOnErrorContainer,
    errorContainer = CalmError,
    onErrorContainer = CalmOnError,
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
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors

    CompositionLocalProvider(LocalMediShelfStatusColors provides statusColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = MediShelfTypography,
            shapes = MediShelfShapes,
            content = content,
        )
    }
}
