package com.volfor.ondori.features.alarm.data.services

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.volfor.ondori.app.notifications.MissedAlarmNotificationBuilder
import com.volfor.ondori.app.notifications.hasPostNotificationPermission
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.MissedAlarmNotifier
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MissedAlarmNotifierImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationBuilder: MissedAlarmNotificationBuilder,
) : MissedAlarmNotifier {

    @SuppressLint("MissingPermission")
    override fun notifyMissed(alarm: Alarm) {
        if (!context.hasPostNotificationPermission()) return

        val notification = notificationBuilder.build(alarm)
        NotificationManagerCompat.from(context.applicationContext).notify(
            Constants.Notifications.MISSED_ALARM_NOTIFICATION_TAG,
            alarm.id.toInt(),
            notification,
        )
    }
}
