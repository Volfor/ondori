@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.volfor.ondori.app.theme.OndoriTheme
import java.util.Calendar

@Composable
fun AlarmTimePicker(
    initialHour: Int? = null,
    initialMinute: Int? = null,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    var showDial by remember { mutableStateOf(true) }

    AlarmTimePickerContent(
        initialHour = initialHour,
        initialMinute = initialMinute,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        onToggle = { showDial = !showDial },
        showDial = showDial,
    )
}

@Composable
private fun AlarmTimePickerContent(
    initialHour: Int? = null,
    initialMinute: Int? = null,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
    onToggle: () -> Unit,
    showDial: Boolean,
) {
    val currentTime = Calendar.getInstance().apply {
        add(Calendar.MINUTE, 1)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour ?: currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialMinute ?: currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    val toggleIcon = if (showDial) {
        Icons.Outlined.Keyboard
    } else {
        Icons.Outlined.AccessTime
    }

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) },
        toggle = {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = "Time picker type toggle",
                )
            }
        },
    ) {
        if (showDial) {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        } else {
            TimeInput(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun TimePickerDialog(
    title: String = "Select Time",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm) { Text("OK") }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmTimePickerDial() {
    OndoriTheme {
        AlarmTimePickerContent(
            onConfirm = {},
            onDismiss = {},
            onToggle = {},
            showDial = true,
        )
    }
}

@Preview
@Composable
fun PreviewAlarmTimePickerInput() {
    OndoriTheme {
        AlarmTimePickerContent(
            onConfirm = {},
            onDismiss = {},
            onToggle = {},
            showDial = false,
        )
    }
}
