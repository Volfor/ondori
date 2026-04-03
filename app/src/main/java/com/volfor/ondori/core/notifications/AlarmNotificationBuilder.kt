package com.volfor.ondori.core.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.volfor.ondori.R
import com.volfor.ondori.core.AlarmService
import com.volfor.ondori.features.alarm.presentation.activities.AlarmRingingActivity
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmNotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun build(alarmId: Long): Notification {
        return NotificationCompat.Builder(
            context.applicationContext,
            Constants.Notifications.FIRING_ALARMS_CHANNEL_ID,
        ).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("Alarm")
            setContentText("Wake up")
            setPriority(NotificationCompat.PRIORITY_MAX)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setFullScreenIntent(createFullScreenIntent(alarmId), true)
            setAutoCancel(false)
            setOngoing(true)
            addAction(
                R.drawable.ic_launcher_foreground,
                "Snooze",
                createSnoozeIntent(alarmId),
            )
            addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                createStopIntent(alarmId),
            )
            setDeleteIntent(createDismissIntent(alarmId))
        }.build()
    }

    private fun createFullScreenIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context.applicationContext, AlarmRingingActivity::class.java).apply {
            putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getActivity(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_FULL_SCREEN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createSnoozeIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context.applicationContext, AlarmService::class.java).apply {
            action = AlarmService.Action.ACTION_NOTIFICATION_SNOOZE
            putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getService(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_SNOOZE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createStopIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context.applicationContext, AlarmService::class.java).apply {
            action = AlarmService.Action.ACTION_NOTIFICATION_STOP
            putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getService(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_STOP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createDismissIntent(alarmId: Long): PendingIntent {
        val intent = Intent(context.applicationContext, AlarmService::class.java).apply {
            action = AlarmService.Action.ACTION_NOTIFICATION_DISMISS
            putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getService(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_DISMISS,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}