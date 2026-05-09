package com.volfor.ondori.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.app.time.LocalIs24HourFormat
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import java.time.DayOfWeek

val previewAlarms = listOf(
    Alarm(
        id = 1,
        hour = 9,
        minute = 5,
        enabled = true,
    ),
    Alarm(
        id = 2,
        hour = 13,
        minute = 0,
        enabled = true,
        label = "Morning zazen",
    ),
    Alarm(
        id = 3,
        hour = 18,
        minute = 45,
        enabled = false,
        label = "Gym",
        repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
    ),
)

class AlarmPreviewParameterProvider : PreviewParameterProvider<Alarm> {
    override val values = previewAlarms.asSequence()
}

@Composable
fun OndoriPreview(
    darkTheme: Boolean = false,
    is24HourFormat: Boolean = true,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalIs24HourFormat provides is24HourFormat) {
        OndoriTheme(isDarkTheme = darkTheme) {
            content()
        }
    }
}