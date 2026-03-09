package com.volfor.ondori.features.alarm.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.volfor.ondori.R
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.components.AlarmItemCard
import com.volfor.ondori.features.alarm.presentation.viewmodels.AlarmViewModel

@Composable
fun AlarmsScreen(
    onNavigateToFirst: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState

    AlarmsContent(
        loading = uiState.isLoading,
        alarms = uiState.items,
        onNavigateToFirst = onNavigateToFirst,
        onSetAlarm = {
            viewModel.setAlarmInOneMinute()
        },
        onCancelAlarm = {
            viewModel.cancelAlarm()
        },
    )
}

@Composable
private fun AlarmsContent(
    loading: Boolean,
    alarms: List<Alarm>,
    onNavigateToFirst: () -> Unit,
    onSetAlarm: () -> Unit,
    onCancelAlarm: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Button(onClick = onNavigateToFirst) {
            Text(text = stringResource(R.string.previous))
        }
        Button(onClick = onSetAlarm) {
            Text(text = "Set alarm")
        }
        Button(onClick = onCancelAlarm) {
            Text(text = "Cancel alarm")
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(alarms) { alarm ->
                AlarmItemCard(alarm)
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmsScreen() {
    OndoriTheme {
        Surface {
            AlarmsContent(
                loading = false,
                alarms = listOf(
                    Alarm(
                        id = 1,
                        label = "Test 1",
                        enabled = true,
                    ),
                    Alarm(
                        id = 2,
                        label = "Test 2",
                        enabled = true,
                    ),
                    Alarm(
                        id = 3,
                        label = "Test 3",
                        enabled = false,
                    ),
                ),
                onNavigateToFirst = {}, onSetAlarm = {}, onCancelAlarm = {},
            )
        }
    }
}
