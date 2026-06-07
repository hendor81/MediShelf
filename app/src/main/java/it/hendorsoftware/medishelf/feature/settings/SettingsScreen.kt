package it.hendorsoftware.medishelf.feature.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import it.hendorsoftware.medishelf.BuildConfig
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.core.designsystem.component.MediShelfTopAppBar
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.domain.model.SettingsThemeMode

/**
 * Route delle Impostazioni.
 *
 * @param modifier modificatore Compose applicato alla schermata.
 * @param viewModel ViewModel Hilt della feature.
 */
@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onThemeClick = viewModel::onThemeClick,
        onThemeSelected = viewModel::onThemeSelected,
        onThemeDialogDismissed = viewModel::onThemeDialogDismissed,
        onThresholdClick = viewModel::onThresholdClick,
        onThresholdInputChanged = viewModel::onThresholdInputChanged,
        onThresholdSaveClick = viewModel::onThresholdSaveClick,
        onThresholdDialogDismissed = viewModel::onThresholdDialogDismissed,
        onNotificationsEnabledChanged = viewModel::onNotificationsEnabledChanged,
        onRemovalPreferenceClick = viewModel::onRemovalPreferenceClick,
        onRemovalPreferenceSelected = viewModel::onRemovalPreferenceSelected,
        onRemovalPreferenceDialogDismissed = viewModel::onRemovalPreferenceDialogDismissed,
        onClearArchiveClick = viewModel::onClearArchiveClick,
        onClearArchiveConfirmed = viewModel::onClearArchiveConfirmed,
        onClearArchiveDismissed = viewModel::onClearArchiveDismissed,
        onPrivacyClick = viewModel::onPrivacyClick,
        onPrivacyDialogDismissed = viewModel::onPrivacyDialogDismissed,
        onFeedbackShown = viewModel::onFeedbackShown,
        modifier = modifier,
    )
}

/**
 * Schermata stateless delle impostazioni base Free.
 *
 * @param uiState stato completo della schermata.
 * @param onThemeClick callback per aprire la scelta tema.
 * @param onThemeSelected callback per salvare il tema.
 * @param onThemeDialogDismissed callback di chiusura dialog tema.
 * @param onThresholdClick callback per aprire la modifica soglia.
 * @param onThresholdInputChanged callback per il campo soglia.
 * @param onThresholdSaveClick callback per salvare la soglia.
 * @param onThresholdDialogDismissed callback di chiusura dialog soglia.
 * @param onNotificationsEnabledChanged callback per aggiornare le notifiche.
 * @param onRemovalPreferenceClick callback per aprire l'azione predefinita.
 * @param onRemovalPreferenceSelected callback per salvare l'azione predefinita.
 * @param onRemovalPreferenceDialogDismissed callback di chiusura dialog azione.
 * @param onClearArchiveClick callback per aprire conferma svuota archivio.
 * @param onClearArchiveConfirmed callback per eliminare l'archivio confermato.
 * @param onClearArchiveDismissed callback di chiusura conferma archivio.
 * @param onPrivacyClick callback per aprire privacy e disclaimer.
 * @param onPrivacyDialogDismissed callback di chiusura privacy.
 * @param onFeedbackShown callback per consumare feedback.
 * @param modifier modificatore Compose applicato alla schermata.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeClick: () -> Unit,
    onThemeSelected: (SettingsThemeMode) -> Unit,
    onThemeDialogDismissed: () -> Unit,
    onThresholdClick: () -> Unit,
    onThresholdInputChanged: (String) -> Unit,
    onThresholdSaveClick: () -> Unit,
    onThresholdDialogDismissed: () -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onRemovalPreferenceClick: () -> Unit,
    onRemovalPreferenceSelected: (Boolean) -> Unit,
    onRemovalPreferenceDialogDismissed: () -> Unit,
    onClearArchiveClick: () -> Unit,
    onClearArchiveConfirmed: () -> Unit,
    onClearArchiveDismissed: () -> Unit,
    onPrivacyClick: () -> Unit,
    onPrivacyDialogDismissed: () -> Unit,
    onFeedbackShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val archiveEmptyMessage = stringResource(R.string.settings_archive_empty_feedback)
    val archiveClearedMessage = stringResource(R.string.settings_archive_cleared_feedback)

    LaunchedEffect(uiState.feedback) {
        when (uiState.feedback) {
            SettingsFeedback.ARCHIVE_EMPTY -> snackbarHostState.showSnackbar(archiveEmptyMessage)
            SettingsFeedback.ARCHIVE_CLEARED -> snackbarHostState.showSnackbar(archiveClearedMessage)
            null -> return@LaunchedEffect
        }
        onFeedbackShown()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MediShelfTopAppBar(title = stringResource(R.string.settings_screen_title))
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        SettingsContent(
            uiState = uiState,
            onThemeClick = onThemeClick,
            onThresholdClick = onThresholdClick,
            onNotificationsEnabledChanged = onNotificationsEnabledChanged,
            onRemovalPreferenceClick = onRemovalPreferenceClick,
            onClearArchiveClick = onClearArchiveClick,
            onPrivacyClick = onPrivacyClick,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        )
    }

    if (uiState.isThemeDialogVisible) {
        SettingsThemeDialog(
            selectedThemeMode = uiState.themeMode,
            onThemeSelected = onThemeSelected,
            onDismiss = onThemeDialogDismissed,
        )
    }

    if (uiState.isThresholdDialogVisible) {
        SettingsThresholdDialog(
            input = uiState.thresholdInput,
            error = uiState.thresholdInputError,
            onInputChanged = onThresholdInputChanged,
            onSaveClick = onThresholdSaveClick,
            onDismiss = onThresholdDialogDismissed,
        )
    }

    if (uiState.isRemovalPreferenceDialogVisible) {
        SettingsRemovalPreferenceDialog(
            preferArchiveOverDelete = uiState.preferArchiveOverDelete,
            onPreferenceSelected = onRemovalPreferenceSelected,
            onDismiss = onRemovalPreferenceDialogDismissed,
        )
    }

    if (uiState.isClearArchiveDialogVisible) {
        SettingsClearArchiveDialog(
            isActionInProgress = uiState.isActionInProgress,
            onConfirm = onClearArchiveConfirmed,
            onDismiss = onClearArchiveDismissed,
        )
    }

    if (uiState.isPrivacyDialogVisible) {
        SettingsPrivacyDialog(onDismiss = onPrivacyDialogDismissed)
    }
}

@Composable
private fun SettingsContent(
    uiState: SettingsUiState,
    onThemeClick: () -> Unit,
    onThresholdClick: () -> Unit,
    onNotificationsEnabledChanged: (Boolean) -> Unit,
    onRemovalPreferenceClick: () -> Unit,
    onClearArchiveClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (uiState.isLoading || uiState.isActionInProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(MediShelfDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingLarge),
        ) {
            SettingsCard {
                SettingsNavigationRow(
                    iconResId = R.drawable.tema,
                    titleResId = R.string.settings_theme_title,
                    descriptionResId = R.string.settings_theme_description,
                    value = stringResource(uiState.themeMode.labelResId()),
                    onClick = onThemeClick,
                    testTag = SettingsTestTags.ThemeRow,
                )
                SettingsDivider()
                SettingsNavigationRow(
                    iconResId = R.drawable.soglia_scadenza,
                    titleResId = R.string.settings_threshold_title,
                    descriptionResId = R.string.settings_threshold_description,
                    value = stringResource(
                        R.string.settings_threshold_value,
                        uiState.expiringThresholdDays,
                    ),
                    onClick = onThresholdClick,
                    testTag = SettingsTestTags.ThresholdRow,
                )
                SettingsDivider()
                SettingsSwitchRow(
                    iconResId = R.drawable.notifica_off,
                    titleResId = R.string.settings_notifications_title,
                    descriptionResId = R.string.settings_notifications_description,
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = onNotificationsEnabledChanged,
                    testTag = SettingsTestTags.NotificationsSwitch,
                )
            }

            SettingsSectionTitle(titleResId = R.string.settings_data_section_title)

            SettingsCard {
                SettingsNavigationRow(
                    iconResId = R.drawable.archivio,
                    titleResId = R.string.settings_default_action_title,
                    descriptionResId = R.string.settings_default_action_description,
                    value = stringResource(
                        if (uiState.preferArchiveOverDelete) {
                            R.string.settings_default_action_archive
                        } else {
                            R.string.settings_default_action_delete
                        },
                    ),
                    onClick = onRemovalPreferenceClick,
                    testTag = SettingsTestTags.RemovalPreferenceRow,
                )
                SettingsDivider()
                SettingsNavigationRow(
                    iconResId = R.drawable.cestino,
                    titleResId = R.string.settings_clear_archive_title,
                    descriptionResId = R.string.settings_clear_archive_description,
                    onClick = onClearArchiveClick,
                    isDestructive = true,
                    testTag = SettingsTestTags.ClearArchiveRow,
                )
            }

            SettingsSectionTitle(titleResId = R.string.settings_info_section_title)

            SettingsCard {
                SettingsNavigationRow(
                    iconResId = R.drawable.privacy,
                    titleResId = R.string.settings_privacy_title,
                    onClick = onPrivacyClick,
                    testTag = SettingsTestTags.PrivacyRow,
                )
                SettingsDivider()
                SettingsInfoRow(
                    iconResId = R.drawable.info,
                    titleResId = R.string.settings_app_version_title,
                    value = stringResource(
                        R.string.settings_app_version_value,
                        BuildConfig.VERSION_NAME,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = SettingsLayoutTokens.CardElevation),
        content = content,
    )
}

@Composable
private fun SettingsSectionTitle(
    @StringRes titleResId: Int,
) {
    Text(
        text = stringResource(titleResId),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SettingsNavigationRow(
    @DrawableRes iconResId: Int,
    @StringRes titleResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @StringRes descriptionResId: Int? = null,
    value: String? = null,
    isDestructive: Boolean = false,
    testTag: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(SettingsLayoutTokens.RowPadding),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowIcon(iconResId = iconResId)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
        ) {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            descriptionResId?.let { description ->
                Text(
                    text = stringResource(description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        value?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    @DrawableRes iconResId: Int,
    @StringRes titleResId: Int,
    @StringRes descriptionResId: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(SettingsLayoutTokens.RowPadding),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowIcon(iconResId = iconResId)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingExtraSmall),
        ) {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(descriptionResId),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SettingsInfoRow(
    @DrawableRes iconResId: Int,
    @StringRes titleResId: Int,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SettingsLayoutTokens.RowPadding),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsRowIcon(iconResId = iconResId)
        Text(
            text = stringResource(titleResId),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsRowIcon(
    @DrawableRes iconResId: Int,
) {
    Box(
        modifier = Modifier.size(SettingsLayoutTokens.IconContainerSize),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier.size(SettingsLayoutTokens.IconSize),
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = SettingsLayoutTokens.DividerAlpha))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsThemeDialog(
    selectedThemeMode: SettingsThemeMode,
    onThemeSelected: (SettingsThemeMode) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        shape = RoundedCornerShape(
            topStart = SettingsLayoutTokens.ThemeSheetCornerRadius,
            topEnd = SettingsLayoutTokens.ThemeSheetCornerRadius,
        ),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = MediShelfDimens.ScreenPadding,
                    vertical = SettingsLayoutTokens.ThemeSheetVerticalPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
        ) {
            Text(
                text = stringResource(R.string.settings_theme_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = MediShelfDimens.SpacingSmall),
            )

            SettingsThemeMode.entries.forEach { themeMode ->
                SettingsThemeOptionRow(
                    themeMode = themeMode,
                    selected = selectedThemeMode == themeMode,
                    onClick = { onThemeSelected(themeMode) },
                )
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SettingsLayoutTokens.ThemeCancelTopPadding)
                    .height(SettingsLayoutTokens.ThemeCancelHeight),
                shape = RoundedCornerShape(SettingsLayoutTokens.ThemeOptionCornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = SettingsLayoutTokens.ThemeCancelContainerAlpha,
                    ),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
            ) {
                Text(text = stringResource(R.string.designsystem_delete_dialog_dismiss))
            }
        }
    }
}

@Composable
private fun SettingsThemeOptionRow(
    themeMode: SettingsThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(SettingsLayoutTokens.ThemeOptionCornerRadius)
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = SettingsLayoutTokens.ThemeOptionBorderAlpha)
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = SettingsLayoutTokens.ThemeSelectedContainerAlpha)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingsLayoutTokens.ThemeOptionHeight)
            .clip(shape)
            .background(containerColor)
            .border(
                border = BorderStroke(
                    width = if (selected) {
                        SettingsLayoutTokens.ThemeSelectedBorderWidth
                    } else {
                        SettingsLayoutTokens.ThemeOptionBorderWidth
                    },
                    color = borderColor,
                ),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = SettingsLayoutTokens.ThemeOptionHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingMedium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(SettingsLayoutTokens.ThemeIconContainerSize)
                .clip(RoundedCornerShape(SettingsLayoutTokens.ThemeIconCornerRadius))
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(
                            alpha = SettingsLayoutTokens.ThemeIconContainerAlpha,
                        )
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(themeMode.iconResId()),
                contentDescription = null,
                modifier = Modifier.size(SettingsLayoutTokens.ThemeIconSize),
            )
        }
        Text(
            text = stringResource(themeMode.labelResId()),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun SettingsThresholdDialog(
    input: String,
    error: SettingsThresholdInputError?,
    onInputChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_threshold_dialog_title)) },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChanged,
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                label = { Text(text = stringResource(R.string.settings_threshold_dialog_label)) },
                supportingText = {
                    Text(
                        text = stringResource(
                            error?.messageResId() ?: R.string.settings_threshold_dialog_support,
                            MediShelfDefaults.MinExpiringThresholdDays,
                            MediShelfDefaults.MaxExpiringThresholdDays,
                        ),
                    )
                },
                isError = error != null,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            Button(onClick = onSaveClick) {
                Text(text = stringResource(R.string.settings_dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.designsystem_delete_dialog_dismiss))
            }
        },
    )
}

@Composable
private fun SettingsRemovalPreferenceDialog(
    preferArchiveOverDelete: Boolean,
    onPreferenceSelected: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_default_action_dialog_title)) },
        text = {
            Column {
                SettingsRadioRow(
                    text = stringResource(R.string.settings_default_action_archive),
                    selected = preferArchiveOverDelete,
                    onClick = { onPreferenceSelected(true) },
                )
                SettingsRadioRow(
                    text = stringResource(R.string.settings_default_action_delete),
                    selected = !preferArchiveOverDelete,
                    onClick = { onPreferenceSelected(false) },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.settings_dialog_close))
            }
        },
    )
}

@Composable
private fun SettingsClearArchiveDialog(
    isActionInProgress: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_clear_archive_dialog_title)) },
        text = { Text(text = stringResource(R.string.settings_clear_archive_dialog_body)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isActionInProgress,
            ) {
                Text(text = stringResource(R.string.settings_clear_archive_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.designsystem_delete_dialog_dismiss))
            }
        },
    )
}

@Composable
private fun SettingsPrivacyDialog(
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.settings_privacy_dialog_title)) },
        text = { Text(text = stringResource(R.string.settings_privacy_dialog_body)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.settings_dialog_close))
            }
        },
    )
}

@Composable
private fun SettingsRadioRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = MediShelfDimens.SpacingSmall),
        horizontalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@StringRes
private fun SettingsThemeMode.labelResId(): Int = when (this) {
    SettingsThemeMode.LIGHT -> R.string.settings_theme_light
    SettingsThemeMode.DARK -> R.string.settings_theme_dark
    SettingsThemeMode.SYSTEM -> R.string.settings_theme_system
}

@DrawableRes
private fun SettingsThemeMode.iconResId(): Int = when (this) {
    SettingsThemeMode.LIGHT -> R.drawable.sole
    SettingsThemeMode.DARK -> R.drawable.luna
    SettingsThemeMode.SYSTEM -> R.drawable.schermo_pc
}

@StringRes
private fun SettingsThresholdInputError.messageResId(): Int? = when (this) {
    SettingsThresholdInputError.BLANK -> R.string.settings_threshold_error_blank
    SettingsThresholdInputError.INVALID_NUMBER -> R.string.settings_threshold_error_invalid
    SettingsThresholdInputError.OUT_OF_RANGE -> R.string.settings_threshold_error_range
}

private object SettingsLayoutTokens {
    val RowPadding = MediShelfDimens.SpacingMedium
    val IconContainerSize = MediShelfDimens.MinInteractiveSize
    val IconSize = MediShelfDimens.BottomNavigationIconSize
    val CardElevation = MediShelfDimens.SpacingExtraSmall
    val ThemeSheetCornerRadius = 28.dp
    val ThemeSheetVerticalPadding = 36.dp
    val ThemeOptionHeight = 94.dp
    val ThemeOptionCornerRadius = 18.dp
    val ThemeOptionHorizontalPadding = 22.dp
    val ThemeIconContainerSize = 48.dp
    val ThemeIconCornerRadius = 15.dp
    val ThemeIconSize = 28.dp
    val ThemeSelectedBorderWidth = 2.dp
    val ThemeOptionBorderWidth = 1.dp
    val ThemeCancelTopPadding = 30.dp
    val ThemeCancelHeight = 64.dp
    const val DividerAlpha = 0.18f
    const val ThemeOptionBorderAlpha = 0.24f
    const val ThemeSelectedContainerAlpha = 0.42f
    const val ThemeIconContainerAlpha = 0.42f
    const val ThemeCancelContainerAlpha = 0.42f
}

/**
 * Preview delle Impostazioni con valori del mockup.
 */
@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    MediShelfTheme {
        SettingsScreen(
            uiState = SettingsUiState(isLoading = false),
            onThemeClick = {},
            onThemeSelected = {},
            onThemeDialogDismissed = {},
            onThresholdClick = {},
            onThresholdInputChanged = {},
            onThresholdSaveClick = {},
            onThresholdDialogDismissed = {},
            onNotificationsEnabledChanged = {},
            onRemovalPreferenceClick = {},
            onRemovalPreferenceSelected = {},
            onRemovalPreferenceDialogDismissed = {},
            onClearArchiveClick = {},
            onClearArchiveConfirmed = {},
            onClearArchiveDismissed = {},
            onPrivacyClick = {},
            onPrivacyDialogDismissed = {},
            onFeedbackShown = {},
        )
    }
}

/**
 * Test tag stabili della schermata Impostazioni.
 */
object SettingsTestTags {
    const val ThemeRow = "settings_theme_row"
    const val ThresholdRow = "settings_threshold_row"
    const val NotificationsSwitch = "settings_notifications_switch"
    const val RemovalPreferenceRow = "settings_removal_preference_row"
    const val ClearArchiveRow = "settings_clear_archive_row"
    const val PrivacyRow = "settings_privacy_row"
}
