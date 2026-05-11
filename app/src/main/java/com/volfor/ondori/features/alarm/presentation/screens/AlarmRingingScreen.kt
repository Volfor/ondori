package com.volfor.ondori.features.alarm.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.BuildConfig
import com.volfor.ondori.R
import com.volfor.ondori.app.time.LocalIs24HourFormat
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.components.SwipeToStopSlider
import com.volfor.ondori.features.alarm.presentation.components.penaltyLevelTint
import com.volfor.ondori.features.alarm.presentation.viewmodels.AlarmRingingViewModel
import com.volfor.ondori.features.punisher.domain.entities.PenaltyLevel
import com.volfor.ondori.utils.OndoriPreview
import com.volfor.ondori.utils.previewAlarms
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AlarmRingingScreen(
    viewModel: AlarmRingingViewModel = hiltViewModel(),
    onAlarmHandled: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val tint = penaltyLevelTint(PenaltyLevel.fromScore(uiState.score))

    Scaffold(
        containerColor = tint,
    ) { paddingValues ->
        LaunchedEffect(uiState.isAlarmHandled) {
            if (uiState.isAlarmHandled) {
                onAlarmHandled()
            }
        }

        AlarmRingingContent(
            modifier = Modifier.padding(paddingValues),
            alarm = uiState.alarm,
            score = uiState.score,
            snoozeMinutes = uiState.snoozeMinutes,
            onSnooze = viewModel::snooze,
            onDismiss = viewModel::dismiss,
        )
    }
}

@Composable
fun AlarmRingingContent(
    modifier: Modifier = Modifier,
    alarm: Alarm?,
    score: Int,
    snoozeMinutes: Int?,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Alarm,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Clock()
            if (alarm?.label != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = alarm.label.toUpperCase(Locale.current),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                )
            }
            Spacer(modifier = Modifier.weight(2f))
            Button(
                onClick = onSnooze,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "SNOOZE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (snoozeMinutes != null) {
                        Text(
                            text = pluralStringResource(
                                R.plurals.minutes_short,
                                snoozeMinutes,
                                snoozeMinutes,
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            SwipeToStopSlider(
                onStop = onDismiss,
            )
            Spacer(modifier = Modifier.height(64.dp))
            if (BuildConfig.DEBUG) {
                Text("Score: $score", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun Clock() {
    var now by remember { mutableStateOf(ZonedDateTime.now()) }

    val is24Hour = LocalIs24HourFormat.current
    val formatter = remember(is24Hour) {
        DateTimeFormatter.ofPattern(
            if (is24Hour) "HH:mm" else "h:mm", java.util.Locale.getDefault()
        )
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            now = ZonedDateTime.now()
            delay(1_000L)
        }
    }

    Text(
        text = now.format(formatter),
        style = MaterialTheme.typography.displayLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 84.sp,
        fontWeight = FontWeight.ExtraBold,
    )
}

@Preview(group = "Light")
@Composable
fun PreviewAlarmRingingScreenLight(
    alarm: Alarm = previewAlarms[1],
    score: Int = -2,
) {
    OndoriPreview {
        Scaffold(
            containerColor = penaltyLevelTint(PenaltyLevel.fromScore(score)),
        ) { paddingValues ->
            AlarmRingingContent(
                modifier = Modifier.padding(paddingValues),
                alarm = alarm,
                score = score,
                snoozeMinutes = 5,
                onSnooze = {},
                onDismiss = {},
            )
        }
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewAlarmRingingScreenDark(
    alarm: Alarm = previewAlarms[2],
    score: Int = -2,
) {
    OndoriPreview(darkTheme = true) {
        Scaffold(
            containerColor = penaltyLevelTint(PenaltyLevel.fromScore(score)),
        ) { paddingValues ->
            AlarmRingingContent(
                modifier = Modifier.padding(paddingValues),
                alarm = alarm,
                score = score,
                snoozeMinutes = 5,
                onSnooze = {},
                onDismiss = {},
            )
        }
    }
}
