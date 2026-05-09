package com.volfor.ondori.app.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import com.volfor.ondori.R
import com.volfor.ondori.app.theme.PenaltyLevelColors
import com.volfor.ondori.core.receivers.AlarmNotificationActionReceiver
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.presentation.activities.AlarmRingingActivity
import com.volfor.ondori.features.punisher.domain.entities.PenaltyLevel
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

class AlarmNotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clock: Clock,
) {

    fun build(alarm: Alarm, score: Int): Notification {
        val date = Date.from(ZonedDateTime.now(clock).toInstant())
        val weekday = DateFormat.format("EEE", date.time).toString()
        val time = DateFormat.getTimeFormat(context.applicationContext).format(date)

        val tint =
            PenaltyLevelColors.argb(PenaltyLevel.fromScore(score), context.applicationContext)

        return NotificationCompat.Builder(
            context.applicationContext,
            Constants.Notifications.FIRING_ALARMS_CHANNEL_ID,
        ).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle(alarm.label ?: "Alarm")
            setContentText("$weekday $time · Swipe to stop")
            setColor(tint)
            setColorized(true)
            setPriority(NotificationCompat.PRIORITY_MAX)
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setFullScreenIntent(createFullScreenIntent(alarm.id), true)
            setAutoCancel(false)
            setOngoing(true)
            addAction(
                R.drawable.ic_launcher_foreground,
                "Snooze",
                createSnoozeIntent(alarm.id),
            )
            addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                createStopIntent(alarm.id),
            )
            setDeleteIntent(createDismissIntent(alarm.id))
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
        val intent =
            Intent(context.applicationContext, AlarmNotificationActionReceiver::class.java).apply {
                action = AlarmNotificationActionReceiver.ACTION_NOTIFICATION_SNOOZE
                putExtra(Constants.EXTRA_ALARM_ID, alarmId)
            }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_SNOOZE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createStopIntent(alarmId: Long): PendingIntent {
        val intent =
            Intent(context.applicationContext, AlarmNotificationActionReceiver::class.java).apply {
                action = AlarmNotificationActionReceiver.ACTION_NOTIFICATION_STOP
                putExtra(Constants.EXTRA_ALARM_ID, alarmId)
            }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_STOP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createDismissIntent(alarmId: Long): PendingIntent {
        val intent =
            Intent(context.applicationContext, AlarmNotificationActionReceiver::class.java).apply {
                action = AlarmNotificationActionReceiver.ACTION_NOTIFICATION_DISMISS
                putExtra(Constants.EXTRA_ALARM_ID, alarmId)
            }
        return PendingIntent.getBroadcast(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_DISMISS,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}