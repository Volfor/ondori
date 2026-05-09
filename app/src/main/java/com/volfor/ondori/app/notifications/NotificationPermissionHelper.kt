package com.volfor.ondori.app.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.volfor.ondori.utils.Constants.Notifications

sealed class AlarmNotificationStatus {
    data object Allowed : AlarmNotificationStatus()

    data object NeedsPostNotificationPermission : AlarmNotificationStatus()

    data object AppNotificationsDisabled : AlarmNotificationStatus()

    data object AlarmChannelDisabled : AlarmNotificationStatus()

    data object FullScreenIntentsDisabled : AlarmNotificationStatus()
}

fun Context.resolveAlarmNotificationStatus(): AlarmNotificationStatus {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPostNotificationPermission()) {
        return AlarmNotificationStatus.NeedsPostNotificationPermission
    }
    if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
        return AlarmNotificationStatus.AppNotificationsDisabled
    }
    val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = manager.getNotificationChannel(Notifications.FIRING_ALARMS_CHANNEL_ID)
    if (channel == null || channel.importance == NotificationManager.IMPORTANCE_NONE) {
        return AlarmNotificationStatus.AlarmChannelDisabled
    }
    if (!canUseFullScreenIntents()) {
        return AlarmNotificationStatus.FullScreenIntentsDisabled
    }
    return AlarmNotificationStatus.Allowed
}

fun Context.hasPostNotificationPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

fun Context.openAppNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        openAppDetailsSettings()
    }
}

fun Context.openAlarmChannelSettings() {
    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        putExtra(Settings.EXTRA_CHANNEL_ID, Notifications.FIRING_ALARMS_CHANNEL_ID)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        openAppNotificationSettings()
    }
}

fun Context.openAppDetailsSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}

fun Context.canUseFullScreenIntents(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        return true
    }
    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    return nm.canUseFullScreenIntent()
}