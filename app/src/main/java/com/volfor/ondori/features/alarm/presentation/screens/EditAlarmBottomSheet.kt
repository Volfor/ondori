@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.volfor.ondori.features.alarm.presentation.screens

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import com.volfor.ondori.R
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound
import com.volfor.ondori.features.alarm.presentation.components.AlarmTimePicker
import com.volfor.ondori.features.alarm.presentation.components.AlarmTimeText
import com.volfor.ondori.features.alarm.presentation.components.OndoriAlertDialog
import com.volfor.ondori.features.alarm.presentation.formatters.soundLabel
import com.volfor.ondori.utils.OndoriPreview
import com.volfor.ondori.utils.previewAlarms
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

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        EditAlarmContent(alarm = alarm, onDelete = {
            onDelete()
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onClose()
                }
            }
        }, onSave = { editedAlarm ->
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
        })
    }
}

@Composable
private fun EditAlarmContent(
    alarm: Alarm,
    onDelete: () -> Unit,
    onSave: (Alarm) -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }

    var editedAlarm by remember(alarm.id) { mutableStateOf(alarm) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        // Time row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AlarmTimeText(editedAlarm.hour, editedAlarm.minute)
            FilledTonalButton(
                onClick = {
                    showTimePicker = true
                },
            ) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Repeat days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        ) {
            val repeatDays = editedAlarm.repeatDays
            DayOfWeek.entries.forEach { day ->
                val letter =
                    day.getDisplayName(TextStyle.NARROW, LocalLocale.current.platformLocale)
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
                    modifier = Modifier.weight(1f),
                    colors = IconButtonDefaults.filledIconToggleButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        checkedContainerColor = MaterialTheme.colorScheme.primary,
                        checkedContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    shapes = IconButtonDefaults.toggleableShapes(
                        shape = RoundedCornerShape(12.dp),
                        checkedShape = RoundedCornerShape(24.dp),
                    ),
                ) {
                    Text(
                        letter,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(
            alarm = editedAlarm,
            onLabelValueChange = { editedAlarm = editedAlarm.copy(label = it) },
            onSoundClick = { showSoundDialog = true },
        )

        Spacer(modifier = Modifier.height(56.dp))

        //Save/Delete buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            FilledTonalButton(
                onClick = {
                    onDelete()
                }, modifier = Modifier
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
            Button(
                onClick = {
                    onSave(editedAlarm)
                }, modifier = Modifier
            ) {
                Text("Save")
            }
        }
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
                    hour = time.hour, minute = time.minute, enabled = enabled
                )
            },
        )

        showSoundDialog -> AlarmSoundDialog(
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
}

@Composable
private fun SettingsSection(
    alarm: Alarm,
    onLabelValueChange: (String) -> Unit,
    onSoundClick: () -> Unit,
) {
    val count = 2

    var value by remember(alarm.id) {
        mutableStateOf(TextFieldValue(alarm.label.orEmpty()))
    }

    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
    ) {
        SettingsListItem(
            index = 0,
            count = count,
            onClick = {
                focusRequester.requestFocus()
                value = value.copy(selection = TextRange(value.text.length))
            },
            leadingContent = {
                Row {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Label,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Label",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            },
        ) {
            BasicTextField(
                value = value,
                onValueChange = {
                    value = it
                    onLabelValueChange(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value.text,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        contentPadding = PaddingValues(0.dp),
                        interactionSource = interactionSource,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedPlaceholderColor = TextFieldDefaults.colors().focusedPlaceholderColor.copy(
                                alpha = 0.7f
                            ),
                            unfocusedPlaceholderColor = TextFieldDefaults.colors().focusedPlaceholderColor.copy(
                                alpha = 0.7f
                            ),
                        ),
                        placeholder = {
                            Text(
                                "Alarm",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        },
                    )
                },
            )
        }
        SettingsListItem(
            index = 1,
            count = count,
            onClick = onSoundClick,
            leadingContent = {
                Row {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Sound",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            },
            content = {
                Text(
                    alarm.soundLabel(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }
}

@Composable
private fun SettingsListItem(
    onClick: () -> Unit,
    enabled: Boolean = true,
    index: Int,
    count: Int,
    colors: ListItemColors = ListItemDefaults.colors(),
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shapes = ListItemDefaults.segmentedShapes(index = index, count = count)
    SegmentedListItem(
        onClick = onClick,
        enabled = enabled,
        shapes = shapes.copy(
            selectedShape = shapes.shape,
            draggedShape = shapes.shape,
            hoveredShape = shapes.shape,
            pressedShape = shapes.shape,
            focusedShape = shapes.shape,
        ),
        colors = colors,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
        content = content,
    )
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

@Preview(group = "Light")
@Composable
fun PreviewEditAlarmBottomSheetLight(
    alarm: Alarm = previewAlarms[0]
) {
    OndoriPreview {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            EditAlarmContent(
                alarm = alarm,
                onSave = {},
                onDelete = {},
            )
        }
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewEditAlarmBottomSheetDark(
    alarm: Alarm = previewAlarms[0]
) {
    OndoriPreview(darkTheme = true) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            EditAlarmContent(
                alarm = alarm,
                onSave = {},
                onDelete = {},
            )
        }
    }
}

@Preview(group = "Light")
@Composable
fun PreviewAlarmSoundDialogLight() {
    OndoriPreview {
        AlarmSoundDialog(
            onConfirm = {},
            onDismiss = {},
            initialSound = AlarmSound.Silent,
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewAlarmSoundDialogDark() {
    OndoriPreview(darkTheme = true) {
        AlarmSoundDialog(
            onConfirm = {},
            onDismiss = {},
            initialSound = AlarmSound.Silent,
        )
    }
}