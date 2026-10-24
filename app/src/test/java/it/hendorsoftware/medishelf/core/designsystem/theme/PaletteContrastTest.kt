package it.hendorsoftware.medishelf.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

/** Previene regressioni del contrasto del testo nelle palette chiare e scure. */
class PaletteContrastTest {
    /** Le coppie usate per testo e pulsanti mantengono almeno 4,5:1. */
    @Test fun shouldKeepReadableThemeText() {
        listOf(LightColorScheme, DarkColorScheme).forEach { scheme ->
            listOf(
                scheme.onPrimary to scheme.primary,
                scheme.onSecondary to scheme.secondary,
                scheme.onBackground to scheme.background,
                scheme.onSurface to scheme.surface,
                scheme.onSurfaceVariant to scheme.surface,
                scheme.onSurfaceVariant to scheme.surfaceVariant,
                scheme.error to scheme.surface,
                scheme.onError to scheme.error,
            ).forEach { (text, background) -> assertContrast(text, background) }
        }
    }

    /** Il badge resta leggibile per ogni stato in entrambi i temi. */
    @Test fun shouldKeepReadableStatusBadges() {
        listOf(LightStatusColors, DarkStatusColors).forEach { colors ->
            listOf(
                colors.validContent to colors.validContainer,
                colors.expiringContent to colors.expiringContainer,
                colors.expiredContent to colors.expiredContainer,
                colors.outOfStockContent to colors.outOfStockContainer,
                colors.noExpirationContent to colors.noExpirationContainer,
                colors.archivedContent to colors.archivedContainer,
            ).forEach { (text, background) -> assertContrast(text, background) }
        }
    }

    /** Rapporto WCAG calcolato sulla luminanza relativa sRGB di colori opachi. */
    private fun assertContrast(text: Color, background: Color) {
        val light = maxOf(text.luminance(), background.luminance())
        val dark = minOf(text.luminance(), background.luminance())
        val ratio = (light + 0.05f) / (dark + 0.05f)
        assertTrue("Contrasto insufficiente: $ratio", ratio >= 4.5f)
    }
}
