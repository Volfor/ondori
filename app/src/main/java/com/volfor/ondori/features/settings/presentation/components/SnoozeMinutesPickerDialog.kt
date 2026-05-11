@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.settings.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.volfor.ondori.R
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import com.volfor.ondori.utils.OndoriPreview

@Composable
fun SnoozeMinutesPickerDialog(
    selectedValue: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        SnoozeMinutesContent(
            selectedValue = selectedValue,
            onSelect = onSelect,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun SnoozeMinutesContent(
    selectedValue: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val range = SettingsRepository.SNOOZE_MINUTES_RANGE
    val selectedIndex = (selectedValue - range.first).coerceIn(0, range.count() - 1)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = selectedIndex,
    )

    LaunchedEffect(Unit) {
        listState.scrollToItem(selectedIndex)
    }

    Surface(
        shape = AlertDialogDefaults.shape,
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.settings_snooze_duration_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(
                    start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp
                ),
            )
            HorizontalDivider()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp),
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                ) {
                    items(range.toList()) { minutes ->
                        val selected = minutes == selectedValue
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selected,
                                    onClick = { onSelect(minutes) },
                                    role = Role.RadioButton,
                                )
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = null,
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = pluralStringResource(
                                    R.plurals.minutes,
                                    minutes,
                                    minutes,
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewSnoozeMinutesContent() {
    OndoriPreview {
        SnoozeMinutesContent(
            selectedValue = 4,
            onSelect = {},
            onDismiss = {},
        )
    }
}