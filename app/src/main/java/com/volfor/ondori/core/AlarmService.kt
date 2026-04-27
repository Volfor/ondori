package com.volfor.ondori.core

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.volfor.ondori.app.notifications.AlarmNotificationBuilder
import com.volfor.ondori.di.ApplicationScope
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.MissAlarmUseCase
import com.volfor.ondori.utils.Constants
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import com.volfor.ondori.utils.Constants.Notifications
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : LifecycleService() {

    companion object {
        const val ACTION_STOP_RINGING_FOR_ALARM = "com.volfor.ondori.ACTION_STOP_RINGING_FOR_ALARM"
    }

    @Inject
    lateinit var getAlarm: GetAlarmUseCase

    @Inject
    lateinit var missAlarm: MissAlarmUseCase

    @Inject
    lateinit var alarmSoundPlayer: AlarmSoundPlayer

    @Inject
    lateinit var alarmVibrator: AlarmVibrator

    @Inject
    lateinit var notificationBuilder: AlarmNotificationBuilder

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    private var ringingAlarmId: Long? = null
    private var missTimeoutJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d("AlarmService", "Alarm service started")
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L

        if (alarmId == -1L) {
            Log.w("AlarmService", "Started without alarm_id, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        if (intent?.action == ACTION_STOP_RINGING_FOR_ALARM) {
            if (alarmId == ringingAlarmId || ringingAlarmId == null) {
                ringingAlarmId = null
                missTimeoutJob?.cancel()
                missTimeoutJob = null
                stopSelf()
            }
            return START_NOT_STICKY
        }

        lifecycleScope.launch {
            val alarm = getAlarm(alarmId)
            if (alarm == null) {
                stopSelf()
                return@launch
            }

            ringingAlarmId = alarmId
            val notification = notificationBuilder.build(alarm)

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
            alarmSoundPlayer.play(alarm.sound)

            missTimeoutJob = launch {
                delay(Constants.Alarm.MISSED_TIMEOUT_MILLIS)
                Log.d("AlarmService", "Alarm missed after timeout: $alarmId")
                applicationScope.launch { missAlarm(alarmId) }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        missTimeoutJob?.cancel()
        missTimeoutJob = null
        alarmVibrator.stop()
        alarmSoundPlayer.stop()
        super.onDestroy()
    }
}