package com.volfor.ondori.app

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import com.volfor.ondori.utils.Constants.Notifications
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OndoriApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(buildFiringAlarmsChannel())
        manager.createNotificationChannel(buildMissedAlarmsChannel())
    }

    private fun buildFiringAlarmsChannel(): NotificationChannel {
        return NotificationChannel(
            Notifications.FIRING_ALARMS_CHANNEL_ID,
            "Firing alarms",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
        }
    }

    private fun buildMissedAlarmsChannel(): NotificationChannel {
        return NotificationChannel(
            Notifications.MISSED_ALARMS_CHANNEL_ID,
            "Missed alarms",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(true)
        }
    }
}
