package it.hendorsoftware.medishelf.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import it.hendorsoftware.medishelf.R

/**
 * Famiglia tipografica Sans-serif moderna usata da MediShelf.
 */
private val MediShelfFontFamily = FontFamily(
    Font(R.font.poppins_regular, weight = FontWeight.Normal),
    Font(R.font.poppins_semibold, weight = FontWeight.SemiBold),
)

private val DefaultTypography = Typography()

private fun TextStyle.withMediShelfFont(fontWeight: FontWeight): TextStyle =
    copy(
        fontFamily = MediShelfFontFamily,
        fontWeight = fontWeight,
    )

/**
 * Scala tipografica base dell'app.
 *
 * Allinea i pesi al riferimento Figma: 400 per corpo/input e 600 per titoli,
 * label e pulsanti.
 */
val MediShelfTypography = Typography(
    displayLarge = DefaultTypography.displayLarge.withMediShelfFont(FontWeight.SemiBold),
    displayMedium = DefaultTypography.displayMedium.withMediShelfFont(FontWeight.SemiBold),
    displaySmall = DefaultTypography.displaySmall.withMediShelfFont(FontWeight.SemiBold),
    headlineLarge = DefaultTypography.headlineLarge.withMediShelfFont(FontWeight.SemiBold),
    headlineMedium = TextStyle(
        fontFamily = MediShelfFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    headlineSmall = DefaultTypography.headlineSmall.withMediShelfFont(FontWeight.SemiBold),
    titleLarge = TextStyle(
        fontFamily = MediShelfFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = MediShelfFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = DefaultTypography.titleSmall.withMediShelfFont(FontWeight.SemiBold),
    bodyLarge = TextStyle(
        fontFamily = MediShelfFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = MediShelfFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = DefaultTypography.bodySmall.withMediShelfFont(FontWeight.Normal),
    labelLarge = TextStyle(
        fontFamily = MediShelfFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = DefaultTypography.labelMedium.withMediShelfFont(FontWeight.SemiBold),
    labelSmall = DefaultTypography.labelSmall.withMediShelfFont(FontWeight.SemiBold),
)
