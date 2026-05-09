package com.volfor.ondori.app.notifications

import android.app.Notification
import android.content.Context
import android.text.format.DateFormat
import androidx.core.app.NotificationCompat
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
            setContentTitle(alarm.label ?: "Missed alarm")
            setContentText("Alarm was scheduled for $time")
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setCategory(NotificationCompat.CATEGORY_EVENT)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(true)
        }.build()
    }
}
