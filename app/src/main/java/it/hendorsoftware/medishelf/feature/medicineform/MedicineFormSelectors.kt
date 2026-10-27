package it.hendorsoftware.medishelf.feature.medicineform

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.component.MedicinePackageForms
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Menu dei formati: consente anche di rimuovere la selezione opzionale. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MedicinePackageFormSelector(value: String, enabled: Boolean, onChange: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val options = stringArrayResource(R.array.medicine_package_forms)
    val labels = stringArrayResource(R.array.medicine_package_form_labels)
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if (enabled) expanded = it }) {
        OutlinedTextField(
            value = MedicinePackageForms.labelFor(value).orEmpty(),
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.medicine_form_package_form_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text(stringResource(R.string.medicine_form_not_set)) }, onClick = {
                onChange("")
                expanded = false
            })
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(labels[index], style = MaterialTheme.typography.bodyLarge) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(MedicinePackageForms.iconFor(option)),
                            contentDescription = null,
                            modifier = Modifier.size(MediShelfDimens.BottomNavigationIconSize),
                        )
                    },
                    onClick = {
                        onChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

/** Calendario senza input manuale; UTC evita slittamenti della data tra fusi orari. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MedicineExpirationSelector(value: String, enabled: Boolean, onChange: (String) -> Unit) {
    var showCalendar by rememberSaveable { mutableStateOf(false) }
    val calendarLabel = stringResource(R.string.medicine_form_expiration_date_label)
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(stringResource(R.string.medicine_form_expiration_date_label)) },
            supportingText = { Text(stringResource(R.string.medicine_form_calendar_support)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(Modifier.matchParentSize().semantics { contentDescription = calendarLabel }
            .clickable(enabled = enabled) { showCalendar = true })
    }
    if (showCalendar) {
        val initialDate = remember(value) {
            runCatching { LocalDate.parse(value).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() }.getOrNull()
        }
        val state = rememberDatePickerState(initialSelectedDateMillis = initialDate)
        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toString())
                    }
                    showCalendar = false
                }) { Text(stringResource(R.string.medicine_form_date_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { onChange(""); showCalendar = false }) {
                    Text(stringResource(R.string.medicine_form_date_clear))
                }
            },
        ) { DatePicker(state = state, showModeToggle = false) }
    }
}

/** Preview del menu dei formati. */
@Preview
@Composable
private fun PackageFormSelectorPreview() {
    MediShelfTheme { MedicinePackageFormSelector("pomata", true, {}) }
}

/** Preview del campo calendario. */
@Preview
@Composable
private fun ExpirationSelectorPreview() {
    MediShelfTheme { MedicineExpirationSelector("2026-12-31", true, {}) }
}
