package com.volfor.ondori.core

import android.app.Notification
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.volfor.ondori.utils.Constants
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : LifecycleService(), AlarmServiceHost {

    companion object {
        const val ACTION_STOP_RINGING_FOR_ALARM = "com.volfor.ondori.ACTION_STOP_RINGING_FOR_ALARM"
    }

    @Inject
    lateinit var coordinator: AlarmServiceCoordinator

    override fun onCreate() {
        super.onCreate()
        lifecycleScope.launch {
            coordinator.observeRingingAlarm(this@AlarmService)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L
        Log.d("AlarmService", "Alarm service started: action=${intent?.action}, alarmId=$alarmId")

        coordinator.handleStartCommand(intent?.action, alarmId, this)
        return START_NOT_STICKY
    }

    override fun startInForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                Constants.Notifications.FIRING_ALARM_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED,
            )
        } else {
            startForeground(
                Constants.Notifications.FIRING_ALARM_NOTIFICATION_ID,
                notification,
            )
        }
    }

    override fun stop() {
        stopSelf()
    }

    override fun onDestroy() {
        coordinator.release()
        super.onDestroy()
    }
}
