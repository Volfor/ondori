package com.volfor.ondori.features.alarm.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.volfor.ondori.app.notifications.AlarmNotificationStatus
import com.volfor.ondori.utils.OndoriPreview

@Composable
fun NotificationPermissionCard(
    status: AlarmNotificationStatus,
    onRequestPermission: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenChannelSettings: () -> Unit,
) {
    lateinit var title: String
    lateinit var body: String
    lateinit var actionLabel: String
    lateinit var action: () -> Unit

    if (status == AlarmNotificationStatus.Allowed) {
        error("NotificationPermissionCard cannot be used with AlarmNotificationStatus.Allowed")
    }

    when (status) {
        AlarmNotificationStatus.NeedsPostNotificationPermission -> {
            title = "Notifications permission required"
            body = "Without notification permission, your alarms won't ring or show on this device."
            actionLabel = "Allow"
            action = onRequestPermission
        }

        AlarmNotificationStatus.AppNotificationsDisabled -> {
            title = "Notifications are turned off"
            body = "Turn on notifications for Ondori so alarms can alert you."
            actionLabel = "Settings"
            action = onOpenNotificationSettings
        }

        AlarmNotificationStatus.AlarmChannelDisabled -> {
            title = "Alarm channel is off"
            body = "The Firing alarms channel is disabled. Turn it on so alarm alerts work."
            actionLabel = "Settings"
            action = onOpenChannelSettings
        }

        AlarmNotificationStatus.FullScreenIntentsDisabled -> {
            title = "Full-screen notifications are off"
            body =
                "Ondori needs permission to show full-screen alarm alerts. Please \"Allow full-screen notifications\" for this app in the system settings."
            actionLabel = "Settings"
            action = onOpenNotificationSettings
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(onClick = action) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Preview(group = "Light")
@Composable
fun PreviewNotificationPermissionCardPermissionMissingLight() {
    OndoriPreview {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.NeedsPostNotificationPermission,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewNotificationPermissionCardDisabledLight() {
    OndoriPreview {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.AppNotificationsDisabled,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewNotificationPermissionCardChannelDisabledLight() {
    OndoriPreview {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.AlarmChannelDisabled,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Light")
@Composable
fun PreviewNotificationPermissionCardFullScreenDisabledLight() {
    OndoriPreview {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.FullScreenIntentsDisabled,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewNotificationPermissionCardPermissionMissingDark() {
    OndoriPreview(darkTheme = true) {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.NeedsPostNotificationPermission,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewNotificationPermissionCardDisabledDark() {
    OndoriPreview(darkTheme = true) {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.AppNotificationsDisabled,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewNotificationPermissionCardChannelDisabledDark() {
    OndoriPreview(darkTheme = true) {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.AlarmChannelDisabled,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}

@Preview(group = "Dark")
@Composable
fun PreviewNotificationPermissionCardFullScreenDisabledDark() {
    OndoriPreview(darkTheme = true) {
        NotificationPermissionCard(
            status = AlarmNotificationStatus.FullScreenIntentsDisabled,
            onOpenNotificationSettings = {},
            onRequestPermission = {},
            onOpenChannelSettings = {},
        )
    }
}