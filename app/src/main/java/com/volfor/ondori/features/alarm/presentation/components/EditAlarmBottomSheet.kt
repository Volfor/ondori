@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun EditAlarmBottomSheet(
    alarm: Alarm,
    onDismissRequest: () -> Unit,
    onSave: (Alarm) -> Unit,
    onClose: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    var showTimePicker by remember { mutableStateOf(false) }

    var hour by remember(alarm) { mutableIntStateOf(alarm.hour) }
    var minute by remember(alarm) { mutableIntStateOf(alarm.minute) }
    var label by remember(alarm) { mutableStateOf(alarm.label ?: "") }
    var repeatDays by remember(alarm) { mutableStateOf(alarm.repeatDays) }
    var enabled by remember(alarm) { mutableStateOf(alarm.enabled) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Time", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = {
                        showTimePicker = true
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text("%02d:%02d".format(hour, minute))
                }
            }
            // Label
            OutlinedTextField(
                value = label,
                onValueChange = {
                    label = it
                },
                placeholder = {
                    Text("Alarm")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            // Repeat days
            Text("Repeat", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                DayOfWeek.entries.forEach { day ->
                    val displayName = day.getDisplayName(TextStyle.NARROW, Locale.getDefault())
                    FilterChip(
                        selected = day in repeatDays,
                        onClick = {
                            repeatDays = if (day in repeatDays) {
                                repeatDays - day
                            } else {
                                repeatDays + day
                            }
                        },
                        label = {
                            Text(displayName)
                        },
                    )
                }
            }
            // Enabled switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Enabled", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        enabled = it
                    },
                )
            }
            //Save button
            Button(
                onClick = {
                    onSave(
                        alarm.copy(
                            hour = hour,
                            minute = minute,
                            label = label.ifBlank { null },
                            repeatDays = repeatDays,
                            enabled = enabled,
                        )
                    )
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            onClose()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Save")
            }
        }
    }

    when {
        showTimePicker -> AlarmTimePicker(
            initialHour = hour,
            initialMinute = minute,
            onDismiss = {
                showTimePicker = false
            },
            onConfirm = { time ->
                showTimePicker = false

                hour = time.hour
                minute = time.minute
            },
        )
    }
}

@Preview
@Composable
fun PreviewEditAlarmBottomSheet() {
    EditAlarmBottomSheet(
        alarm = Alarm(
            id = 1,
            hour = 12,
            minute = 35,
            enabled = true,
        ),
        onDismissRequest = {},
        onSave = {},
        onClose = {},
    )
}