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
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkValidContainer,
    onPrimaryContainer = DarkValidContent,
    inversePrimary = SagePrimary,
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkValidContainer,
    onSecondaryContainer = DarkValidContent,
    tertiary = DarkOutOfStockContent,
    onTertiary = DarkOutOfStockContainer,
    tertiaryContainer = DarkOutOfStockContainer,
    onTertiaryContainer = DarkOutOfStockContent,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceTint = DarkPrimary,
    surfaceDim = DarkBackground,
    surfaceBright = DarkSurfaceVariant,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurface,
    surfaceContainerHighest = DarkSurfaceVariant,
    inverseSurface = WarmSurface,
    inverseOnSurface = WarmOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = DarkExpiredContent,
    onError = DarkExpiredContainer,
    errorContainer = DarkExpiredContainer,
    onErrorContainer = DarkExpiredContent,
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
