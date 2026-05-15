package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.core.designsystem.theme.mediShelfStatusColors

/**
 * Stato visuale supportato dal badge dei medicinali.
 *
 * Il tipo e' volutamente UI-only: le feature potranno mappare qui lo stato di
 * dominio senza far dipendere il design system dal layer domain.
 */
enum class MedicineStatusBadgeStatus(
    @param:StringRes val labelResId: Int,
) {
    Valid(R.string.medicine_status_valid),
    ExpiringSoon(R.string.medicine_status_expiring_soon),
    Expired(R.string.medicine_status_expired),
    OutOfStock(R.string.medicine_status_out_of_stock),
    NoExpiration(R.string.medicine_status_no_expiration),
    Archived(R.string.medicine_status_archived),
}

/**
 * Badge compatto per comunicare lo stato di un medicinale.
 *
 * @param status stato visuale da rappresentare.
 * @param modifier modificatore Compose applicato al contenitore.
 */
@Composable
fun MedicineStatusBadge(
    status: MedicineStatusBadgeStatus,
    modifier: Modifier = Modifier,
) {
    val colors = status.badgeColors()

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(MediShelfDimens.CornerLarge))
            .background(colors.container)
            .padding(
                horizontal = MediShelfDimens.BadgeHorizontalPadding,
                vertical = MediShelfDimens.BadgeVerticalPadding,
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(status.labelResId),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = colors.content,
        )
    }
}

/**
 * Risolve i colori semantici del badge in base allo stato richiesto.
 *
 * @return coppia di colori container/content coerente con il tema corrente.
 */
@Composable
private fun MedicineStatusBadgeStatus.badgeColors(): BadgeColors {
    val statusColors = MaterialTheme.mediShelfStatusColors
    return when (this) {
        MedicineStatusBadgeStatus.Valid -> BadgeColors(
            container = statusColors.validContainer,
            content = statusColors.validContent,
        )
        MedicineStatusBadgeStatus.ExpiringSoon -> BadgeColors(
            container = statusColors.expiringContainer,
            content = statusColors.expiringContent,
        )
        MedicineStatusBadgeStatus.Expired -> BadgeColors(
            container = statusColors.expiredContainer,
            content = statusColors.expiredContent,
        )
        MedicineStatusBadgeStatus.OutOfStock -> BadgeColors(
            container = statusColors.outOfStockContainer,
            content = statusColors.outOfStockContent,
        )
        MedicineStatusBadgeStatus.NoExpiration -> BadgeColors(
            container = statusColors.noExpirationContainer,
            content = statusColors.noExpirationContent,
        )
        MedicineStatusBadgeStatus.Archived -> BadgeColors(
            container = statusColors.archivedContainer,
            content = statusColors.archivedContent,
        )
    }
}

/**
 * Coppia cromatica usata internamente dal badge di stato.
 */
private data class BadgeColors(
    val container: Color,
    val content: Color,
)

/**
 * Preview dei badge di stato principali.
 */
@Preview(showBackground = true)
@Composable
private fun MedicineStatusBadgePreview() {
    MediShelfTheme {
        Column(
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall)) {
                MedicineStatusBadge(status = MedicineStatusBadgeStatus.Valid)
                MedicineStatusBadge(status = MedicineStatusBadgeStatus.ExpiringSoon)
                MedicineStatusBadge(status = MedicineStatusBadgeStatus.Expired)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall)) {
                MedicineStatusBadge(status = MedicineStatusBadgeStatus.OutOfStock)
                MedicineStatusBadge(status = MedicineStatusBadgeStatus.NoExpiration)
                MedicineStatusBadge(status = MedicineStatusBadgeStatus.Archived)
            }
        }
    }
}
