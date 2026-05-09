@file:OptIn(ExperimentalMaterial3Api::class)

package com.volfor.ondori.features.alarm.presentation.screens

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.R
import com.volfor.ondori.app.notifications.AlarmNotificationStatus
import com.volfor.ondori.app.notifications.hasPostNotificationPermission
import com.volfor.ondori.app.notifications.openAlarmChannelSettings
import com.volfor.ondori.app.notifications.openAppNotificationSettings
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.components.AlarmItemCard
import com.volfor.ondori.features.alarm.presentation.components.AlarmTimePicker
import com.volfor.ondori.features.alarm.presentation.components.NotificationPermissionCard
import com.volfor.ondori.features.alarm.presentation.components.rememberAlarmNotificationStatus
import com.volfor.ondori.features.alarm.presentation.viewmodels.AlarmsViewModel
import com.volfor.ondori.utils.OndoriPreview
import com.volfor.ondori.utils.previewAlarms
import kotlinx.coroutines.launch

@Composable
fun AlarmsScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: AlarmsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val notificationPermissionStatus = rememberAlarmNotificationStatus()
    val requestNotificationPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.markNotificationPermissionAsRequested()
        if (granted) return@rememberLauncherForActivityResult
        scope.launch {

            val result = snackbarHostState.showSnackbar(
                message = "Turn on notifications so alarms can go off.",
                actionLabel = "Settings",
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                context.openAppNotificationSettings()
            }
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showTimePicker by remember { mutableStateOf(false) }
    var moreMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            viewModel.markNotificationPermissionAsRequested()
            return@LaunchedEffect
        }
        if (context.hasPostNotificationPermission()) {
            viewModel.markNotificationPermissionAsRequested()
            return@LaunchedEffect
        }
        if (!uiState.hasRequestedNotificationPermission) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                actions = {
                    Box {
                        IconButton(onClick = { moreMenuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.action_more),
                            )
                        }
                        MoreMenu(
                            expanded = moreMenuExpanded,
                            onDismissRequest = { moreMenuExpanded = false },
                            onNavigateToSettings = {
                                moreMenuExpanded = false
                                onNavigateToSettings()
                            },
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
        val selectedAlarm = uiState.selectedAlarm

        AlarmsContent(
            notificationPermissionStatus = notificationPermissionStatus,
            hasRequestedNotificationPermission = uiState.hasRequestedNotificationPermission,
            onRequestNotifPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            onOpenChannelSettings = {
                context.openAlarmChannelSettings()
            },
            onOpenAppNotificationSettings = {
                context.openAppNotificationSettings()
            },
            loading = uiState.isLoading,
            alarms = uiState.items,
            score = uiState.score,
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
                onDelete = {
                    viewModel.deleteAlarm(selectedAlarm)
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
    notificationPermissionStatus: AlarmNotificationStatus,
    hasRequestedNotificationPermission: Boolean,
    onRequestNotifPermission: () -> Unit,
    onOpenChannelSettings: () -> Unit,
    onOpenAppNotificationSettings: () -> Unit,
    loading: Boolean,
    alarms: List<Alarm>,
    score: Int,
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
        if (hasRequestedNotificationPermission && notificationPermissionStatus != AlarmNotificationStatus.Allowed) {
            NotificationPermissionCard(
                status = notificationPermissionStatus,
                onRequestPermission = onRequestNotifPermission,
                onOpenNotificationSettings = onOpenAppNotificationSettings,
                onOpenChannelSettings = onOpenChannelSettings,
            )
        }
        Text("Score: $score")
        LazyColumn(
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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

@Composable
private fun MoreMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.widthIn(min = 200.dp),
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.action_settings))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.action_settings)
                )
            },
            onClick = onNavigateToSettings,
        )
    }
}

@Preview
@Composable
fun PreviewAlarmsContent(
    alarms: List<Alarm> = previewAlarms,
) {
    OndoriPreview {
        Surface {
            AlarmsContent(
                notificationPermissionStatus = AlarmNotificationStatus.Allowed,
                hasRequestedNotificationPermission = true,
                onRequestNotifPermission = {},
                onOpenChannelSettings = {},
                onOpenAppNotificationSettings = {},
                loading = false,
                alarms = alarms,
                score = 0,
                onAlarmClick = {},
                onAlarmToggle = { _, _ -> },
                onDelete = {},
            )
        }
    }
}

@Preview
@Composable
fun PreviewAlarmsContentEmpty(
    alarms: List<Alarm> = emptyList(),
) {
    OndoriPreview {
        Surface {
            AlarmsContent(
                notificationPermissionStatus = AlarmNotificationStatus.NeedsPostNotificationPermission,
                hasRequestedNotificationPermission = true,
                onRequestNotifPermission = {},
                onOpenChannelSettings = {},
                onOpenAppNotificationSettings = {},
                loading = false,
                alarms = alarms,
                score = 0,
                onAlarmClick = {},
                onAlarmToggle = { _, _ -> },
                onDelete = {},
            )
        }
    }
}
