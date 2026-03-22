@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.alarm.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.R
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.components.AlarmItemCard
import com.volfor.ondori.features.alarm.presentation.components.AlarmTimePicker
import com.volfor.ondori.features.alarm.presentation.components.EditAlarmBottomSheet
import com.volfor.ondori.features.alarm.presentation.viewmodels.AlarmsViewModel

@Composable
fun AlarmsScreen(
    onNavigateToInfo: () -> Unit,
    viewModel: AlarmsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()

    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.action_settings)
                        )
                    }
                })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showTimePicker = true

//                    scope.launch {
//                        val result = snackbarHostState.showSnackbar(
//                            message = "Replace with your own action",
//                            actionLabel = "Action",
//                            duration = SnackbarDuration.Long
//                        )
//                        if (result == SnackbarResult.ActionPerformed) {
//                            // handle action click
//                        }
//                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add"
                )
            }
        },
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val selectedAlarm = uiState.selectedAlarm

        AlarmsContent(
            loading = uiState.isLoading,
            alarms = uiState.items,
            onAlarmClick = { alarm ->
                viewModel.selectAlarm(alarm)
            },
            onAlarmToggle = { alarm, enabled ->
                viewModel.setAlarmEnabled(alarm, enabled)
            },
            onDelete = { alarm ->
                viewModel.deleteAlarm(alarm)
            },
            modifier = Modifier.padding(paddingValues),
        )

        when {
            showTimePicker -> AlarmTimePicker(
                onDismiss = {
                    showTimePicker = false
                },
                onConfirm = { time ->
                    showTimePicker = false
                    Log.d("AlarmScreen", "Selected time: ${time.hour}:${time.minute}")
                    viewModel.createAlarm(time.hour, time.minute)
                },
            )

            selectedAlarm != null -> EditAlarmBottomSheet(
                alarm = selectedAlarm,
                onDismissRequest = {
                    viewModel.clearSelection()
                },
                onSave = { updatedAlarm ->
                    viewModel.updateAlarm(updatedAlarm)
                },
                onClose = {
                    viewModel.clearSelection()
                },
            )
        }
    }
}

@Composable
private fun AlarmsContent(
    loading: Boolean,
    alarms: List<Alarm>,
    onAlarmClick: (Alarm) -> Unit,
    onAlarmToggle: (Alarm, Boolean) -> Unit,
    onDelete: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                alarms,
                key = { alarm -> alarm.id },
            ) { alarm ->
                AlarmItemCard(
                    alarm,
                    onClick = {
                        onAlarmClick(alarm)
                    },
                    onCheckedChange = { enabled ->
                        onAlarmToggle(alarm, enabled)
                    },
                    onDelete = {
                        onDelete(alarm)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmsContent() {
    OndoriTheme {
        Surface {
            AlarmsContent(
                loading = false,
                alarms = listOf(
                    Alarm(
                        id = 1,
                        hour = 2,
                        minute = 30,
                        enabled = true,
                        label = "Test 1",
                    ),
                    Alarm(
                        id = 2,
                        hour = 9,
                        minute = 0,
                        enabled = true,
                    ),
                    Alarm(
                        id = 3,
                        hour = 14,
                        minute = 45,
                        enabled = false,
                        label = "Test 3",
                    ),
                ),
                onAlarmClick = {},
                onAlarmToggle = { _, _ -> },
                onDelete = {},
            )
        }
    }
}

@Preview
@Composable
fun PreviewAlarmsContentEmpty() {
    OndoriTheme {
        Surface {
            AlarmsContent(
                loading = false,
                alarms = emptyList(),
                onAlarmClick = {},
                onAlarmToggle = { _, _ -> },
                onDelete = {},
            )
        }
    }
}
