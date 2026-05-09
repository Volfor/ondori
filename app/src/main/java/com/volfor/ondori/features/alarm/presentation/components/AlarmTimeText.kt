package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.volfor.ondori.app.time.LocalIs24HourFormat
import com.volfor.ondori.utils.OndoriPreview
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AlarmTimeText(
    hour: Int,
    minute: Int,
    useBoldFonts: Boolean = true,
) {
    val is24Hour = LocalIs24HourFormat.current
    val timeFormatter = remember(is24Hour) {
        DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm", Locale.getDefault())
    }
    val amPmFormatter = remember(is24Hour) {
        DateTimeFormatter.ofPattern(if (is24Hour) "" else "a", Locale.getDefault())
    }

    val time = LocalTime.of(hour, minute)

    Row {
        Text(
            text = time.format(timeFormatter),
            style = MaterialTheme.typography.displayMedium,
            fontSize = 48.sp,
            fontWeight = if (useBoldFonts) FontWeight.ExtraBold else FontWeight.Light,
            modifier = Modifier.alignByBaseline(),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = time.format(amPmFormatter),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp,
            fontWeight = if (useBoldFonts) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.alignByBaseline(),
        )
    }
}

@Preview
@Composable
fun PreviewAlarmTimeText() {
    OndoriPreview(is24HourFormat = true) {
        Surface {
            AlarmTimeText(
                hour = 14,
                minute = 5,
                useBoldFonts = false,
            )
        }
    }
}

@Preview
@Composable
fun PreviewAlarmTimeTextAmPm() {
    OndoriPreview(is24HourFormat = false) {
        Surface {
            AlarmTimeText(
                hour = 14,
                minute = 5,
            )
        }
    }
}