package com.volfor.ondori.features.alarm.presentation.formatters

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import java.time.format.TextStyle
import java.util.Locale

fun Alarm.formattedTime(): String = "%02d:%02d".format(hour, minute)

fun Alarm.formattedRepeatDays(): String = if (repeatDays.isEmpty()) {
    "Not scheduled"
} else repeatDays.sortedBy { it.value }.joinToString(", ") {
    it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
}