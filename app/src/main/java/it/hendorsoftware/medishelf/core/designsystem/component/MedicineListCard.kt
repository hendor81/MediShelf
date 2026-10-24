package it.hendorsoftware.medishelf.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/**
 * Card condivisa da Home, Inventario e Scadenzario.
 *
 * @param name nome del medicinale.
 * @param status stato visuale derivato dalla feature.
 * @param metadata dettagli già formattati, compresi i campi opzionali disponibili.
 * @param onClick apre il dettaglio del medicinale.
 * @param modifier modificatore della card, inclusi eventuali tag di test.
 * @param packageForm formato salvato da associare all'icona.
 * @param badgeBesideName affianca il badge al nome negli elementi della Home.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicineListCard(
    name: String,
    status: MedicineStatusBadgeStatus,
    metadata: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    packageForm: String? = null,
    badgeBesideName: Boolean = false,
    isLowStock: Boolean = false,
) {
    val inlineBadge = badgeBesideName &&
        LocalDensity.current.fontScale < MediShelfDimens.LargeFontScale &&
        LocalConfiguration.current.screenWidthDp >= MediShelfDimens.CompactLayoutWidth.value
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = MediShelfDimens.CardElevation),
    ) {
        Row(
            modifier = Modifier.padding(MediShelfDimens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(MedicinePackageForms.iconFor(packageForm)),
                contentDescription = null,
                modifier = Modifier.size(MediShelfDimens.MedicineIconSize),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (inlineBadge) MedicineStatusBadge(status = status)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
                    verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
                ) {
                    metadata.forEach { detail ->
                        Text(
                            text = detail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                // Il badge va a capo con i dettagli per lasciare leggibile il nome anche su schermi piccoli.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
                    verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
                ) {
                    if (!inlineBadge) MedicineStatusBadge(status = status)
                    if (isLowStock) MedicineStatusBadge(status = MedicineStatusBadgeStatus.LowStock)
                }
            }
        }
    }
}

/** Preview della card con metadati e stato di attenzione. */
@Preview(showBackground = true, widthDp = 320)
@Composable
private fun MedicineListCardPreview() {
    MediShelfTheme {
        MedicineListCard(
            name = "Paracetamolo 500 mg",
            status = MedicineStatusBadgeStatus.ExpiringSoon,
            metadata = listOf("12 compresse", "Scadenza 31/12/2026", "Bagno"),
            onClick = {},
            modifier = Modifier.padding(MediShelfDimens.SpacingMedium),
        )
    }
}

/** Preview della card compatta con nome completo e font al 200%. */
@Preview(showBackground = true, widthDp = 320, fontScale = 2f)
@Composable
private fun MedicineListCardLargeFontPreview() {
    MediShelfTheme {
        MedicineListCard(
            name = "Paracetamolo 500 mg compresse effervescenti",
            status = MedicineStatusBadgeStatus.ExpiringSoon,
            metadata = listOf("Scadenza 31/12/2026"),
            onClick = {}, badgeBesideName = true, isLowStock = true,
        )
    }
}
