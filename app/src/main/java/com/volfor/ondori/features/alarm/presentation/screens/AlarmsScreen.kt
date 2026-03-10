package com.volfor.ondori.features.alarm.presentation.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsScreen(
    onNavigateToInfo: () -> Unit,
    viewModel: AlarmViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uiState = viewModel.uiState

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
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Replace with your own action",
                            actionLabel = "Action",
                            duration = SnackbarDuration.Long
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            // handle action click
                        }
                    }
                }) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = "Add"
                )
            }
        },
    ) { paddingValues ->
        AlarmsContent(
            loading = uiState.isLoading,
            alarms = uiState.items,
            onNavigateToInfo = onNavigateToInfo,
            onSetAlarm = {
                viewModel.setAlarmInOneMinute()
            },
            onCancelAlarm = {
                viewModel.cancelAlarm()
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun AlarmsContent(
    loading: Boolean,
    alarms: List<Alarm>,
    onNavigateToInfo: () -> Unit,
    onSetAlarm: () -> Unit,
    onCancelAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = onNavigateToInfo) {
            Text(text = "Info")
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
fun PreviewAlarmsContent() {
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
                onNavigateToInfo = {}, onSetAlarm = {}, onCancelAlarm = {},
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
                onNavigateToInfo = {}, onSetAlarm = {}, onCancelAlarm = {},
            )
        }
    }
}
