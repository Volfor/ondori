package com.volfor.ondori.features.alarm.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.formatters.formattedTime
import com.volfor.ondori.features.alarm.presentation.viewmodels.AlarmRingingViewModel

@Composable
fun AlarmRingingScreen(
    viewModel: AlarmRingingViewModel = hiltViewModel(),
    onAlarmHandled: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        LaunchedEffect(uiState.isAlarmHandled) {
            if (uiState.isAlarmHandled) {
                onAlarmHandled()
            }
        }

        AlarmRingingContent(
            modifier = Modifier.padding(paddingValues),
            alarm = uiState.alarm,
            onSnooze = viewModel::snooze,
            onDismiss = viewModel::dismiss,
        )
    }
}

@Composable
fun AlarmRingingContent(
    modifier: Modifier = Modifier,
    alarm: Alarm?,
    onSnooze: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = alarm?.formattedTime() ?: "--:--",
                fontSize = 64.sp,
            )
            Text(
                "ALARM ALARM ALARM !!!",
            )
            Spacer(modifier = Modifier.size(48.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = onSnooze,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Snooze,
                        contentDescription = "Snooze",
                        modifier = Modifier.size(48.dp),
                    )
                }
                IconButton(
                    onClick = onDismiss,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AlarmOff,
                        contentDescription = "Dismiss",
                        modifier = Modifier.size(48.dp),
                    )
                }
            }

        }
    }

}

@Preview
@Composable
fun PreviewAlarmRingingScreen() {
    OndoriTheme {
        Surface {
            AlarmRingingContent(
                alarm = Alarm(
                    id = 10,
                    hour = 2,
                    minute = 30,
                    enabled = true,
                ),
                onSnooze = {},
                onDismiss = {},
            )
        }
    }
}
