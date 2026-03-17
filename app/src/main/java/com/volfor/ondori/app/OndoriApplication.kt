package com.volfor.ondori.app

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

const val FIRING_ALARMS_CHANNEL_ID = "firing_alarms_channel"

@HiltAndroidApp
class OndoriApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createFiringAlarmsNotificationChannel()
    }

    private fun createFiringAlarmsNotificationChannel() {
        val channel = NotificationChannel(
            FIRING_ALARMS_CHANNEL_ID,
            "Firing alarms",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
