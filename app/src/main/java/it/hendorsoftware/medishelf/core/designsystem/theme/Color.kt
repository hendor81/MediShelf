package it.hendorsoftware.medishelf.core.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

internal val SagePrimary = Color(0xFF2F6F68)
internal val SageOnPrimary = Color(0xFFFFFFFF)
internal val SagePrimaryContainer = Color(0xFFD8EEE8)
internal val SageOnPrimaryContainer = Color(0xFF103B36)
internal val OliveSecondary = Color(0xFF6D7F55)
internal val OliveOnSecondary = Color(0xFFFFFFFF)
internal val OliveSecondaryContainer = Color(0xFFE4EBCF)
internal val OliveOnSecondaryContainer = Color(0xFF2B341D)
internal val WarmBackground = Color(0xFFF7F4EE)
internal val WarmOnBackground = Color(0xFF1A1C1A)
internal val WarmSurface = Color(0xFFFFFFFF)
internal val WarmOnSurface = Color(0xFF1A1C1A)
internal val SoftSurfaceVariant = Color(0xFFE6EEE9)
internal val SoftOnSurfaceVariant = Color(0xFF46534E)
internal val CalmOutline = Color(0xFF77847E)
internal val CalmError = Color(0xFF9A3D36)
internal val CalmOnError = Color(0xFFFFFFFF)
internal val CalmErrorContainer = Color(0xFFFFDAD5)
internal val CalmOnErrorContainer = Color(0xFF410002)
internal val DarkBackground = Color(0xFF071F20)
internal val DarkOnBackground = Color(0xFFE0E4DF)
internal val DarkSurface = Color(0xFF18211D)
internal val DarkOnSurface = Color(0xFFE0E4DF)
internal val DarkSurfaceVariant = Color(0xFF35443D)
internal val DarkOnSurfaceVariant = Color(0xFFC0CAC3)

private val ValidContainer = Color(0xFFDDEEDB)
private val ValidContent = Color(0xFF24512D)
private val ExpiringContainer = Color(0xFFFFE9B5)
private val ExpiringContent = Color(0xFF6B4A00)
private val ExpiredContainer = Color(0xFFFFDAD5)
private val ExpiredContent = Color(0xFF7A271F)
private val OutOfStockContainer = Color(0xFFE1E7EC)
private val OutOfStockContent = Color(0xFF33434D)
private val NoExpirationContainer = Color(0xFFDCEAF7)
private val NoExpirationContent = Color(0xFF284B63)
private val ArchivedContainer = Color(0xFFE8E4DC)
private val ArchivedContent = Color(0xFF4F4638)

private val DarkValidContainer = Color(0xFF244C2C)
private val DarkValidContent = Color(0xFFC4E7C2)
private val DarkExpiringContainer = Color(0xFF5A4208)
private val DarkExpiringContent = Color(0xFFFFE2A3)
private val DarkExpiredContainer = Color(0xFF73342D)
private val DarkExpiredContent = Color(0xFFFFDAD5)
private val DarkOutOfStockContainer = Color(0xFF34434C)
private val DarkOutOfStockContent = Color(0xFFD6E3EA)
private val DarkNoExpirationContainer = Color(0xFF28495F)
private val DarkNoExpirationContent = Color(0xFFD4E8F8)
private val DarkArchivedContainer = Color(0xFF4B4336)
private val DarkArchivedContent = Color(0xFFECE0D0)

/**
 * Colori semantici per i badge di stato dei medicinali.
 *
 * Ogni coppia container/content mantiene il colore come supporto visivo, mentre
 * il testo del badge resta il segnale principale per accessibilita' e chiarezza.
 */
@Immutable
data class MediShelfStatusColors(
    val validContainer: Color,
    val validContent: Color,
    val expiringContainer: Color,
    val expiringContent: Color,
    val expiredContainer: Color,
    val expiredContent: Color,
    val outOfStockContainer: Color,
    val outOfStockContent: Color,
    val noExpirationContainer: Color,
    val noExpirationContent: Color,
    val archivedContainer: Color,
    val archivedContent: Color,
)

internal val LightStatusColors = MediShelfStatusColors(
    validContainer = ValidContainer,
    validContent = ValidContent,
    expiringContainer = ExpiringContainer,
    expiringContent = ExpiringContent,
    expiredContainer = ExpiredContainer,
    expiredContent = ExpiredContent,
    outOfStockContainer = OutOfStockContainer,
    outOfStockContent = OutOfStockContent,
    noExpirationContainer = NoExpirationContainer,
    noExpirationContent = NoExpirationContent,
    archivedContainer = ArchivedContainer,
    archivedContent = ArchivedContent,
)

internal val DarkStatusColors = MediShelfStatusColors(
    validContainer = DarkValidContainer,
    validContent = DarkValidContent,
    expiringContainer = DarkExpiringContainer,
    expiringContent = DarkExpiringContent,
    expiredContainer = DarkExpiredContainer,
    expiredContent = DarkExpiredContent,
    outOfStockContainer = DarkOutOfStockContainer,
    outOfStockContent = DarkOutOfStockContent,
    noExpirationContainer = DarkNoExpirationContainer,
    noExpirationContent = DarkNoExpirationContent,
    archivedContainer = DarkArchivedContainer,
    archivedContent = DarkArchivedContent,
)

internal val LocalMediShelfStatusColors = staticCompositionLocalOf { LightStatusColors }

/**
 * Espone i colori semantici MediShelf accanto ai token Material 3.
 */
val MaterialTheme.mediShelfStatusColors: MediShelfStatusColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMediShelfStatusColors.current
