package com.volfor.ondori.features.alarm.presentation.formatters

import android.media.RingtoneManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.volfor.ondori.R
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound
import java.time.format.TextStyle
import java.util.Locale

fun Alarm.formattedRepeatDays(): String = if (repeatDays.isEmpty()) {
    "Not scheduled"
} else repeatDays.sortedBy { it.value }.joinToString(", ") {
    it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
}

@Composable
fun Alarm.soundLabel(): String = when (sound) {
    AlarmSound.Default -> stringResource(R.string.alarm_sound_ondori_default)
    AlarmSound.Silent -> stringResource(R.string.alarm_sound_none)
    is AlarmSound.Custom -> {
        val context = LocalContext.current
        val uriString = sound.uri
        runCatching {
            RingtoneManager.getRingtone(context, uriString.toUri())?.getTitle(context)
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: stringResource(R.string.alarm_sound_unknown)
    }
}