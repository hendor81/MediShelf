package it.hendorsoftware.medishelf.feature.home

import it.hendorsoftware.medishelf.feature.onboarding.*
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.AddMedicineActionBar
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineListCard
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.core.designsystem.theme.mediShelfStatusColors

/**
 * Route della Home / Dashboard.
 *
 * @param onAddMedicineClick callback per aprire il form di inserimento.
 * @param onInventoryClick callback per aprire l'inventario.
 * @param onExpiryClick callback per aprire lo scadenzario.
 * @param onAttentionItemClick callback per aprire il dettaglio di una voce in attenzione.
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun HomeRoute(
    onAddMedicineClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onExpiryClick: () -> Unit,
    onAttentionItemClick: (String) -> Unit,
    onSeeAllAttentionClick: () -> Unit = onExpiryClick,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreen(
        uiState = uiState,
        onAddMedicineClick = onAddMedicineClick,
        onInventoryClick = onInventoryClick,
        onExpiryClick = onExpiryClick,
        onSeeAllAttentionClick = onSeeAllAttentionClick,
        onAttentionItemClick = onAttentionItemClick,
        modifier = modifier,
    )
}

/**
 * Schermata stateless della Home / Dashboard.
 *
 * @param uiState stato completo del riepilogo iniziale.
 * @param onAddMedicineClick callback per aprire il form di inserimento.
 * @param onInventoryClick callback per aprire l'inventario.
 * @param onExpiryClick callback per aprire lo scadenzario.
 * @param onAttentionItemClick callback per aprire il dettaglio di una voce in attenzione.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAddMedicineClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onExpiryClick: () -> Unit,
    onAttentionItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSeeAllAttentionClick: () -> Unit = onExpiryClick,
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            HomePrimaryActionBar(onAddMedicineClick = onAddMedicineClick, modifier = Modifier.tutorialTarget(TutorialTarget.Add))
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = MediShelfDimens.SpacingMedium,
                        vertical = MediShelfDimens.SpacingSmallMedium,
                    ),
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingLarge),
            ) {
                HomeHeader()
                HomeSummaryCards(uiState = uiState, modifier = Modifier.tutorialTarget(TutorialTarget.Summary))
                HomeSearchShortcut(onClick = onInventoryClick, modifier = Modifier.tutorialTarget(TutorialTarget.Search))
                HomeAttentionSection(
                    modifier = Modifier.tutorialTarget(TutorialTarget.Medicines),
                    uiState = uiState,
                    onSeeAllClick = onSeeAllAttentionClick,
                    onAttentionItemClick = onAttentionItemClick,
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.home_logo_content_description),
                modifier = Modifier.size(HomeLayoutTokens.LogoSize),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
            ) {
                Text(
                    text = medishelfTitleText(
                        appName = stringResource(R.string.app_name),
                        primaryColor = MaterialTheme.colorScheme.primary,
                        defaultColor = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize * MediShelfDimens.PrimaryTitleScale,
                        lineHeight = MaterialTheme.typography.titleLarge.lineHeight * MediShelfDimens.PrimaryTitleScale,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
                Text(
                    text = stringResource(R.string.home_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
            }
        }

    }
}

private fun medishelfTitleText(
    appName: String,
    primaryColor: Color,
    defaultColor: Color,
    fontWeight: FontWeight,
): AnnotatedString = buildAnnotatedString {
    appName.forEach { character ->
        withStyle(
            SpanStyle(
                color = if (character == 'M' || character == 'S') {
                    primaryColor
                } else {
                    defaultColor
                },
                fontWeight = fontWeight,
            ),
        ) {
            append(character)
        }
    }
}

@Composable
private fun HomeSummaryCards(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
    ) {
        HomeSummaryCard(
            count = uiState.activeMedicineCount,
            label = R.string.home_summary_active,
            icon = R.drawable.ok_icon,
            iconContentDescription = R.string.home_summary_active_content_description,
            style = HomeSummaryCardStyle.Active,
            modifier = Modifier.weight(1f),
        )
        HomeSummaryCard(
            count = uiState.expiringMedicineCount,
            label = R.string.home_summary_expiring,
            icon = R.drawable.in_scadenza_icon,
            iconContentDescription = R.string.home_summary_expiring_content_description,
            style = HomeSummaryCardStyle.Expiring,
            modifier = Modifier.weight(1f),
        )
        HomeSummaryCard(
            count = uiState.expiredMedicineCount,
            label = R.string.home_summary_expired,
            icon = R.drawable.scaduti,
            iconContentDescription = R.string.home_summary_expired_content_description,
            style = HomeSummaryCardStyle.Expired,
            modifier = Modifier.weight(1f),
        )
        HomeSummaryCard(
            count = uiState.lowStockMedicineCount,
            label = R.string.home_summary_low_stock,
            icon = R.drawable.scorta_bassa,
            iconContentDescription = R.string.home_summary_low_stock_content_description,
            style = HomeSummaryCardStyle.LowStock,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun HomeSummaryCard(
    count: Int,
    @StringRes label: Int,
    @DrawableRes icon: Int,
    @StringRes iconContentDescription: Int,
    style: HomeSummaryCardStyle,
    modifier: Modifier = Modifier,
) {
    val colors = style.colors()

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MediShelfDimens.CornerLarge))
            .background(colors.container)
            .height(HomeLayoutTokens.SummaryCardHeight)
            .padding(
                horizontal = MediShelfDimens.SpacingSmall,
                vertical = MediShelfDimens.SpacingSmall,
            )
            .testTag(HomeTestTags.summaryCard(label)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = MediShelfDimens.SpacingExtraSmall,
            // Le etichette su due righe non devono spostare icona e conteggio.
            alignment = Alignment.Top,
        ),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = stringResource(iconContentDescription),
            modifier = Modifier.size(HomeLayoutTokens.SummaryIconSize),
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.content,
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = HomeLayoutTokens.SummaryLabelFontSize,
                lineHeight = HomeLayoutTokens.SummaryLabelLineHeight,
            ),
            // Riduce solo l'etichetta, mantenendo invariata la geometria delle quattro card.
            autoSize = TextAutoSize.StepBased(
                minFontSize = HomeLayoutTokens.SummaryLabelMinFontSize,
                maxFontSize = HomeLayoutTokens.SummaryLabelFontSize,
                stepSize = HomeLayoutTokens.SummaryLabelFontStep,
            ),
            fontWeight = FontWeight.SemiBold,
            color = colors.content,
            maxLines = 2,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HomeSearchShortcut(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MediShelfDimens.CornerLarge))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = MediShelfDimens.SearchBorderWidth,
                color = MaterialTheme.colorScheme.outline.copy(alpha = MediShelfDimens.SearchBorderAlpha),
                shape = RoundedCornerShape(MediShelfDimens.CornerLarge),
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = MediShelfDimens.SpacingMedium,
                vertical = MediShelfDimens.SpacingSmallMedium,
            )
            .testTag(HomeTestTags.SearchShortcut),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.home_search_placeholder),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HomeAttentionSection(
    uiState: HomeUiState,
    onSeeAllClick: () -> Unit,
    onAttentionItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_attention_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.home_see_all),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(MediShelfDimens.CornerSmall))
                    .clickable(onClick = onSeeAllClick)
                    .padding(MediShelfDimens.SpacingExtraSmall)
                    .testTag(HomeTestTags.SeeAllAttention),
            )
        }

        if (uiState.attentionItems.isEmpty()) {
            HomeAttentionEmptyState()
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
            ) {
                uiState.attentionItems.forEach { item ->
                    HomeAttentionItem(
                        item = item,
                        onClick = { onAttentionItemClick(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeAttentionEmptyState(
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = HomeLayoutTokens.CardElevation,
    ) {
        Row(
            modifier = Modifier.padding(MediShelfDimens.SpacingMedium),
            horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.inventario),
                contentDescription = null,
                modifier = Modifier.size(MediShelfDimens.BottomNavigationIconSize),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
            ) {
                Text(
                    text = stringResource(R.string.home_attention_empty_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.home_attention_empty_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeAttentionItem(
    item: HomeAttentionItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MedicineListCard(
        name = item.name,
        packageForm = item.packageForm,
        status = item.status,
        badgeBesideName = true,
        isLowStock = item.isLowStock,
        metadata = listOfNotNull(
            item.quantity,
            item.expirationDate?.let { stringResource(R.string.home_attention_expiration, it) },
            if (item.quantity == null && item.expirationDate == null) {
                item.packageForm ?: stringResource(R.string.home_attention_missing_details)
            } else null,
        ),
        onClick = onClick,
        modifier = modifier.testTag(HomeTestTags.attentionItem(item.id)),
    )
}

@Composable
private fun HomePrimaryActionBar(
    onAddMedicineClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AddMedicineActionBar(
        onAddMedicineClick = onAddMedicineClick,
        modifier = modifier.testTag(HomeTestTags.AddMedicineButton),
    )
}

@Composable
private fun HomeSummaryCardStyle.colors(): HomeSummaryCardColors {
    val statusColors = MaterialTheme.mediShelfStatusColors
    return when (this) {
        HomeSummaryCardStyle.Active -> HomeSummaryCardColors(
            container = statusColors.validContainer,
            content = statusColors.validContent,
        )
        HomeSummaryCardStyle.Expiring -> HomeSummaryCardColors(
            container = statusColors.expiringContainer,
            content = statusColors.expiringContent,
        )
        HomeSummaryCardStyle.Expired -> HomeSummaryCardColors(
            container = statusColors.expiredContainer,
            content = statusColors.expiredContent,
        )
        HomeSummaryCardStyle.LowStock -> HomeSummaryCardColors(
            container = statusColors.outOfStockContainer,
            content = statusColors.outOfStockContent,
        )
    }
}

private enum class HomeSummaryCardStyle {
    Active,
    Expiring,
    Expired,
    LowStock,
}

private data class HomeSummaryCardColors(
    val container: Color,
    val content: Color,
)

private object HomeLayoutTokens {
    val LogoSize = 80.dp
    val SummaryIconSize = 36.dp
    val SummaryLabelFontSize = 11.sp
    val SummaryLabelMinFontSize = 10.sp
    val SummaryLabelFontStep = 0.5.sp
    val SummaryLabelLineHeight = 1.2.em
    val SummaryCardHeight = 123.2.dp
    val CardElevation = 1.dp
}

/**
 * Preview della Home con dati realistici.
 */
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MediShelfTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false,
                activeMedicineCount = 12,
                expiringMedicineCount = 3,
                expiredMedicineCount = 1,
                lowStockMedicineCount = 2,
                attentionItems = sampleHomeAttentionItems,
            ),
            onAddMedicineClick = {},
            onInventoryClick = {},
            onExpiryClick = {},
            onAttentionItemClick = {},
        )
    }
}

/**
 * Preview della Home senza medicinali.
 */
@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    MediShelfTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = false),
            onAddMedicineClick = {},
            onInventoryClick = {},
            onExpiryClick = {},
            onAttentionItemClick = {},
        )
    }
}

/**
 * Preview della Home in caricamento.
 */
@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    MediShelfTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = true),
            onAddMedicineClick = {},
            onInventoryClick = {},
            onExpiryClick = {},
            onAttentionItemClick = {},
        )
    }
}

/**
 * Preview dell'header identitario della Home.
 */
@Preview(showBackground = true)
@Composable
private fun HomeHeaderPreview() {
    MediShelfTheme {
        HomeHeader(modifier = Modifier.padding(MediShelfDimens.ScreenPadding))
    }
}

/**
 * Preview delle card riepilogo della Home.
 */
@Preview(showBackground = true)
@Preview(name = "Riepilogo su schermo stretto", showBackground = true, widthDp = 320)
@Composable
private fun HomeSummaryCardsPreview() {
    MediShelfTheme {
        HomeSummaryCards(
            uiState = HomeUiState(
                isLoading = false,
                activeMedicineCount = 12,
                expiringMedicineCount = 3,
                expiredMedicineCount = 1,
                lowStockMedicineCount = 2,
            ),
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview di una singola card riepilogo.
 */
@Preview(showBackground = true)
@Composable
private fun HomeSummaryCardPreview() {
    MediShelfTheme {
        HomeSummaryCard(
            count = 3,
            label = R.string.home_summary_expiring,
            icon = R.drawable.in_scadenza_icon,
            iconContentDescription = R.string.home_summary_expiring_content_description,
            style = HomeSummaryCardStyle.Expiring,
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview della scorciatoia di ricerca.
 */
@Preview(showBackground = true)
@Composable
private fun HomeSearchShortcutPreview() {
    MediShelfTheme {
        HomeSearchShortcut(
            onClick = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview della sezione attenzione con item.
 */
@Preview(showBackground = true)
@Composable
private fun HomeAttentionSectionPreview() {
    MediShelfTheme {
        HomeAttentionSection(
            uiState = HomeUiState(
                isLoading = false,
                attentionItems = sampleHomeAttentionItems,
            ),
            onSeeAllClick = {},
            onAttentionItemClick = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview dello stato vuoto della sezione attenzione.
 */
@Preview(showBackground = true)
@Composable
private fun HomeAttentionEmptyStatePreview() {
    MediShelfTheme {
        HomeAttentionEmptyState(modifier = Modifier.padding(MediShelfDimens.ScreenPadding))
    }
}

/**
 * Preview di una voce da tenere d'occhio.
 */
@Preview(showBackground = true)
@Composable
private fun HomeAttentionItemPreview() {
    MediShelfTheme {
        HomeAttentionItem(
            item = sampleHomeAttentionItems.first(),
            onClick = {},
            modifier = Modifier.padding(MediShelfDimens.ScreenPadding),
        )
    }
}

/**
 * Preview della CTA primaria della Home.
 */
@Preview(showBackground = true)
@Composable
private fun HomePrimaryActionBarPreview() {
    MediShelfTheme {
        HomePrimaryActionBar(onAddMedicineClick = {})
    }
}

private val sampleHomeAttentionItems = listOf(
    HomeAttentionItemUiModel(
        id = "1",
        name = "Ibuprofene 400 mg",
        packageForm = "Compresse",
        expirationDate = "15/05/2026",
        quantity = "20 compresse",
        status = MedicineStatusBadgeStatus.ExpiringSoon,
    ),
    HomeAttentionItemUiModel(
        id = "2",
        name = "Amoxicillina 500 mg",
        packageForm = "Compresse",
        expirationDate = "10/05/2026",
        quantity = "12 compresse",
        status = MedicineStatusBadgeStatus.Expired,
    ),
    HomeAttentionItemUiModel(
        id = "3",
        name = "Paracetamolo 650 mg",
        packageForm = "Compresse",
        expirationDate = "07/04/2026",
        quantity = "16 compresse",
        status = MedicineStatusBadgeStatus.ExpiringSoon,
    ),
)

/**
 * Test tag stabili della schermata Home.
 */
object HomeTestTags {
    const val SearchShortcut = "home_search_shortcut"
    const val SeeAllAttention = "home_see_all_attention"
    const val AddMedicineButton = "home_add_medicine_button"

    /**
     * Restituisce il tag della card riepilogo associata alla label.
     *
     * @param labelResId risorsa stringa della label visualizzata.
     * @return test tag stabile della card.
     */
    fun summaryCard(@StringRes labelResId: Int): String = "home_summary_card_$labelResId"

    /**
     * Restituisce il tag della card associata al medicinale in attenzione.
     *
     * @param medicineId identificativo della voce visualizzata.
     * @return test tag stabile dell'item.
     */
    fun attentionItem(medicineId: String): String = "home_attention_item_$medicineId"
}
