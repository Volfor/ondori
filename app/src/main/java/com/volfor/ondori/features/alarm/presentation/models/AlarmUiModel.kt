package com.volfor.ondori.features.alarm.presentation.models

import com.volfor.ondori.features.alarm.domain.entities.Alarm

data class AlarmUiModel(
    val id: Long,
    val labelText: String?,
    val timeText: String,
    val enabled: Boolean,
)

fun Alarm.toUiModel(): AlarmUiModel = AlarmUiModel(
    id = id,
    labelText = label,
    timeText = "%02d:%02d".format(hour, minute),
    enabled = enabled,
)
