@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.alarm.presentation.screens

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import com.volfor.ondori.R
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound
import com.volfor.ondori.features.alarm.presentation.components.AlarmTimePicker
import com.volfor.ondori.features.alarm.presentation.components.OndoriAlertDialog
import com.volfor.ondori.features.alarm.presentation.formatters.formattedTime
import com.volfor.ondori.features.alarm.presentation.formatters.soundLabel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.TextStyle

@Composable
fun EditAlarmBottomSheet(
    alarm: Alarm,
    onDismissRequest: () -> Unit,
    onSave: (Alarm) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var showTimePicker by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    var editedAlarm by remember(alarm.id) { mutableStateOf(alarm) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // Time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(editedAlarm.formattedTime(), style = MaterialTheme.typography.displayMedium)
                TextButton(
                    onClick = {
                        showTimePicker = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                ) {
                    Text("Edit")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Label
            Text(
                "Label".toUpperCase(Locale.current),
                style = MaterialTheme.typography.titleSmall,
                color = OndoriTheme.extraColors.title,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = editedAlarm.label ?: "",
                onValueChange = { editedAlarm = editedAlarm.copy(label = it) },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedPlaceholderColor = TextFieldDefaults.colors().focusedPlaceholderColor.copy(
                        alpha = 0.5f
                    ),
                    unfocusedPlaceholderColor = TextFieldDefaults.colors().focusedPlaceholderColor.copy(
                        alpha = 0.5f
                    ),
                ),
                placeholder = {
                    Text("Label")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Repeat days
            Text(
                "Repeat".toUpperCase(Locale.current),
                style = MaterialTheme.typography.titleSmall,
                color = OndoriTheme.extraColors.title,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                val repeatDays = editedAlarm.repeatDays
                DayOfWeek.entries.forEach { day ->
                    val letter = day.getDisplayName(TextStyle.NARROW, java.util.Locale.getDefault())
                    FilledIconToggleButton(
                        checked = day in repeatDays,
                        onCheckedChange = {
                            editedAlarm = editedAlarm.copy(
                                repeatDays = if (day in repeatDays) {
                                    repeatDays - day
                                } else {
                                    repeatDays + day
                                }
                            )
                        },
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            contentColor = OndoriTheme.extraColors.title,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text(
                            letter,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            //Sound
            Text(
                "Sound".toUpperCase(Locale.current),
                style = MaterialTheme.typography.titleSmall,
                color = OndoriTheme.extraColors.title,
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { showSoundDialog = true },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = "Sound",
                        tint = OndoriTheme.extraColors.secondaryIconTint,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        editedAlarm.soundLabel(),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = "Add",
                        tint = OndoriTheme.extraColors.title,
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))

            //Save/Delete buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                FilledTonalButton(
                    onClick = {
                        onDelete()
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onClose()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                ) {
                    Text("Delete")
                }
                Button(
                    onClick = {
                        onSave(
                            editedAlarm.copy(
                                label = editedAlarm.label?.ifBlank { null }?.trim()
                            )
                        )
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onClose()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp),
                ) {
                    Text("Save")
                }
            }
        }
    }

    if (showSoundDialog) {
        AlarmSoundDialog(
            onConfirm = { selectedSound ->
                showSoundDialog = false
                editedAlarm = editedAlarm.copy(sound = selectedSound)
            },
            onDismiss = {
                showSoundDialog = false
            },
            initialSound = editedAlarm.sound,
        )
    }

    when {
        showTimePicker -> AlarmTimePicker(
            initialHour = editedAlarm.hour,
            initialMinute = editedAlarm.minute,
            onDismiss = {
                showTimePicker = false
            },
            onConfirm = { time ->
                showTimePicker = false

                val hasChanged = time.hour != editedAlarm.hour || time.minute != editedAlarm.minute
                val enabled = editedAlarm.enabled || hasChanged

                editedAlarm = editedAlarm.copy(
                    hour = time.hour,
                    minute = time.minute,
                    enabled = enabled
                )
            },
        )
    }
}

@Composable
private fun AlarmSoundDialog(
    onConfirm: (AlarmSound) -> Unit,
    onDismiss: () -> Unit,
    initialSound: AlarmSound,
) {

    var selectedSound by remember { mutableStateOf(initialSound) }

    val ringtoneLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val uri = result.data?.let { data ->
            IntentCompat.getParcelableExtra(
                data,
                RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                Uri::class.java,
            )
        } ?: return@rememberLauncherForActivityResult

        selectedSound = AlarmSound.Custom(uri.toString())
        onConfirm(selectedSound)
    }

    OndoriAlertDialog(
        title = stringResource(R.string.alarm_sound_dialog_title),
        onConfirm = {
            onConfirm(selectedSound)
        },
        onDismiss = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            SoundOption(
                title = stringResource(R.string.alarm_sound_none),
                onClick = {
                    selectedSound = AlarmSound.Silent
                },
                selected = selectedSound == AlarmSound.Silent,
            )
            SoundOption(
                title = stringResource(R.string.alarm_sound_ondori_default),
                onClick = {
                    selectedSound = AlarmSound.Default
                },
                selected = selectedSound == AlarmSound.Default,
            )
            val pickerTitle = stringResource(R.string.alarm_sound_picker_title)
            SoundOption(
                title = stringResource(R.string.alarm_sound_choose_system),
                onClick = {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, pickerTitle)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)

                        val sound = selectedSound
                        if (sound is AlarmSound.Custom) {
                            putExtra(
                                RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                sound.uri.toUri(),
                            )
                        }
                    }
                    ringtoneLauncher.launch(intent)
                },
                selected = selectedSound is AlarmSound.Custom,
            )
        }
    }
}

@Composable
private fun SoundOption(
    title: String,
    onClick: () -> Unit,
    selected: Boolean,
) {
    CompositionLocalProvider(
        LocalRippleConfiguration provides RippleConfiguration(
            color = MaterialTheme.colorScheme.primary,
        )
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = if (selected) {
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                )
                Spacer(modifier = Modifier.weight(1f))
                RadioButton(selected = selected, onClick = null)
            }
        }
    }
}

@Preview
@Composable
fun PreviewEditAlarmBottomSheet() {
    OndoriTheme {
        EditAlarmBottomSheet(
            alarm = Alarm(
                id = 1,
                hour = 12,
                minute = 35,
                enabled = true,
                repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
            ),
            onDismissRequest = {},
            onSave = {},
            onDelete = {},
            onClose = {},
        )
    }
}

@Preview
@Composable
fun PreviewAlarmSoundDialog() {
    OndoriTheme {
        AlarmSoundDialog(
            onConfirm = {},
            onDismiss = {},
            initialSound = AlarmSound.Silent,
        )
    }
}