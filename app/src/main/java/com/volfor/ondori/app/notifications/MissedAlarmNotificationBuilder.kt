package com.volfor.ondori.app.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
import com.volfor.ondori.MainActivity
import com.volfor.ondori.R
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import java.time.ZonedDateTime
import java.util.Date
import javax.inject.Inject

class MissedAlarmNotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clock: Clock,
) {

    fun build(alarm: Alarm): Notification {
        val date = Date.from(
            ZonedDateTime.now(clock).withHour(alarm.hour).withMinute(alarm.minute).withSecond(0)
                .toInstant()
        )
        val time = DateFormat.getTimeFormat(context.applicationContext).format(date)

        return NotificationCompat.Builder(
            context.applicationContext,
            Constants.Notifications.MISSED_ALARMS_CHANNEL_ID,
        ).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("Missed alarm${if (alarm.label != null) " • ${alarm.label}" else ""}")
            setContentText("Alarm was scheduled for $time")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setCategory(NotificationCompat.CATEGORY_EVENT)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
            setContentIntent(createContentIntent())
        }.build()
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(context.applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context.applicationContext,
            Constants.RequestCodes.ALARM_NOTIFICATION_MISSED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
