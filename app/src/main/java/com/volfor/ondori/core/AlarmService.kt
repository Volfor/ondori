package com.volfor.ondori.core

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.volfor.ondori.app.notifications.AlarmNotificationBuilder
import com.volfor.ondori.di.ApplicationScope
import com.volfor.ondori.features.alarm.domain.services.RingingAlarmStore
import com.volfor.ondori.features.alarm.domain.usecases.ApplyPenaltyAndRescheduleEnabledAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.MissAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.GetScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.GetIncreasingVolumeEnabledUseCase
import com.volfor.ondori.utils.Constants
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import com.volfor.ondori.utils.Constants.Notifications
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class AlarmService : LifecycleService() {

    companion object {
        const val ACTION_STOP_RINGING_FOR_ALARM = "com.volfor.ondori.ACTION_STOP_RINGING_FOR_ALARM"
    }

    @Inject
    lateinit var getAlarm: GetAlarmUseCase

    @Inject
    lateinit var getScore: GetScoreUseCase

    @Inject
    lateinit var missAlarm: MissAlarmUseCase

    @Inject
    lateinit var applyPenaltyAndRescheduleEnabledAlarms: ApplyPenaltyAndRescheduleEnabledAlarmsUseCase

    @Inject
    lateinit var getIncreasingVolumeEnabled: GetIncreasingVolumeEnabledUseCase

    @Inject
    lateinit var ringingAlarmStore: RingingAlarmStore

    @Inject
    lateinit var alarmSoundPlayer: AlarmSoundPlayer

    @Inject
    lateinit var alarmVibrator: AlarmVibrator

    @Inject
    lateinit var notificationBuilder: AlarmNotificationBuilder

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    private var missTimeoutJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d("AlarmService", "Alarm service started: action=${intent?.action}")
        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L

        if (alarmId == -1L) {
            Log.w("AlarmService", "Started without alarm_id, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        val previousAlarmId = ringingAlarmStore.ringingAlarmId.value

        if (intent?.action == ACTION_STOP_RINGING_FOR_ALARM) {
            if (alarmId == previousAlarmId || previousAlarmId == null) {
                ringingAlarmStore.clear()
                missTimeoutJob?.cancel()
                missTimeoutJob = null
                stopSelf()
            }
            return START_NOT_STICKY
        }

        if (previousAlarmId == alarmId) {
            Log.d("AlarmService", "Alarm $alarmId already ringing, ignoring duplicate start")
            return START_NOT_STICKY
        }

        lifecycleScope.launch {
            val alarm = getAlarm(alarmId) ?: run {
                stopSelf()
                return@launch
            }
            val score = getScore()

            ringingAlarmStore.setRingingAlarm(alarmId)

            if (previousAlarmId != null) {
                Log.d(
                    "AlarmService",
                    "Alarm $previousAlarmId already ringing, replacing it with newest alarm $alarmId"
                )

                missTimeoutJob?.cancel()
                missTimeoutJob = null
                applicationScope.launch { missAlarm(previousAlarmId) }
            }

            val notification = notificationBuilder.build(alarm, score)

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
            val increasingVolume = getIncreasingVolumeEnabled()
            alarmSoundPlayer.play(alarm.sound, gradualVolumeIncrease = increasingVolume)

            missTimeoutJob = launch {
                delay(Constants.Alarm.MISSED_TIMEOUT_MILLIS.milliseconds)
                Log.d("AlarmService", "Alarm missed after timeout: $alarmId")
                applicationScope.launch {
                    missAlarm(alarmId)
                    applyPenaltyAndRescheduleEnabledAlarms()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        ringingAlarmStore.clear()
        missTimeoutJob?.cancel()
        missTimeoutJob = null
        alarmVibrator.stop()
        alarmSoundPlayer.stop()
        super.onDestroy()
    }
}