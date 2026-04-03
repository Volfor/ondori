package com.volfor.ondori.core

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.volfor.ondori.R
import com.volfor.ondori.core.notifications.AlarmNotificationBuilder
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import com.volfor.ondori.utils.Constants.Notifications
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : LifecycleService() {

    companion object Action {
        const val ACTION_STOP_RINGING_FOR_ALARM = "com.volfor.ondori.ACTION_STOP_RINGING_FOR_ALARM"
        const val ACTION_NOTIFICATION_SNOOZE = "com.volfor.ondori.ACTION_SNOOZE"
        const val ACTION_NOTIFICATION_STOP = "com.volfor.ondori.ACTION_STOP"
        const val ACTION_NOTIFICATION_DISMISS = "com.volfor.ondori.ACTION_DISMISS"
    }

    @Inject
    lateinit var snoozeAlarm: SnoozeAlarmUseCase

    @Inject
    lateinit var dismissAlarm: DismissAlarmUseCase

    @Inject
    lateinit var alarmSoundPlayer: AlarmSoundPlayer

    @Inject
    lateinit var alarmVibrator: AlarmVibrator

    @Inject
    lateinit var notificationBuilder: AlarmNotificationBuilder

    private var ringingAlarmId: Long? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d("AlarmService", "Alarm service started")
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L

        if (alarmId == -1L) {
            Log.w("AlarmService", "Started without alarm_id, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent?.action) {
            ACTION_STOP_RINGING_FOR_ALARM -> {
                if (alarmId == ringingAlarmId || ringingAlarmId == null) {
                    ringingAlarmId = null
                    stopSelf()
                }
                return START_NOT_STICKY
            }

            ACTION_NOTIFICATION_SNOOZE -> {
                lifecycleScope.launch {
                    snoozeAlarm(alarmId)
                }
                return START_NOT_STICKY
            }

            ACTION_NOTIFICATION_DISMISS, ACTION_NOTIFICATION_STOP -> {
                lifecycleScope.launch {
                    dismissAlarm(alarmId)
                }
                return START_NOT_STICKY
            }
        }

        ringingAlarmId = alarmId
        val notification = notificationBuilder.build(alarmId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                Notifications.FIRING_ALARM_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED,
            )
        } else {
            startForeground(
                Notifications.FIRING_ALARM_NOTIFICATION_ID,
                notification,
            )
        }

        alarmVibrator.vibrate()
        alarmSoundPlayer.play(R.raw.rooster)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        alarmVibrator.stop()
        alarmSoundPlayer.stop()
        super.onDestroy()
    }
}