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
internal val OliveSecondary = Color(0xFF697B51)
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
// Palette campionata dal riferimento scuro fornito nella issue #92.
internal val DarkPrimary = Color(0xFF62C7BE)
internal val DarkOnPrimary = Color(0xFF003735)
internal val DarkSecondary = Color(0xFF7BADA8)
internal val DarkOnBackground = Color(0xFFF2F7F4)
internal val DarkSurface = Color(0xFF102B2C)
internal val DarkOnSurface = Color(0xFFF2F7F4)
internal val DarkSurfaceVariant = Color(0xFF143C3A)
internal val DarkOnSurfaceVariant = Color(0xFFA7B8B5)
internal val DarkOutline = Color(0xFF7BADA8)
internal val DarkOutlineVariant = Color(0xFF345151)

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

internal val DarkValidContainer = Color(0xFF143C3A)
internal val DarkValidContent = Color(0xFFCFF8F1)
private val DarkExpiringContainer = Color(0xFF3D3214)
private val DarkExpiringContent = Color(0xFFFFE9A8)
internal val DarkExpiredContainer = Color(0xFF442020)
internal val DarkExpiredContent = Color(0xFFFFD0D0)
internal val DarkOutOfStockContainer = Color(0xFF2D2647)
internal val DarkOutOfStockContent = Color(0xFFE4DBFF)
private val DarkNoExpirationContainer = Color(0xFF102B2C)
private val DarkNoExpirationContent = Color(0xFFA7B8B5)
private val DarkArchivedContainer = Color(0xFF102B2C)
private val DarkArchivedContent = Color(0xFFA7B8B5)

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
