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
        createFiringAlarmsNotificationChannel()
    }

    private fun createFiringAlarmsNotificationChannel() {
        val channel = NotificationChannel(
            Notifications.FIRING_ALARMS_CHANNEL_ID,
            "Firing alarms",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
            setShowBadge(false)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
