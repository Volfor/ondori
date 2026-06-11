package com.volfor.ondori.core

import android.app.Notification
import com.volfor.ondori.app.notifications.AlarmNotificationBuilder
import com.volfor.ondori.di.ApplicationScope
import com.volfor.ondori.features.alarm.domain.services.RingingAlarmStore
import com.volfor.ondori.features.alarm.domain.usecases.ApplyPenaltyAndRescheduleEnabledAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.MissAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.GetScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.GetIncreasingVolumeEnabledUseCase
import com.volfor.ondori.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/** Android side effects [AlarmServiceCoordinator] requires from the hosting service. */
interface AlarmServiceHost {
    fun startInForeground(notification: Notification)
    fun stop()
}

/**
 * Framework-free logic of [AlarmService]: translates start commands and ringing-state
 * changes into ringing, missing alarms, and stopping the service.
 */
class AlarmServiceCoordinator @Inject constructor(
    private val getAlarm: GetAlarmUseCase,
    private val getScore: GetScoreUseCase,
    private val missAlarm: MissAlarmUseCase,
    private val applyPenaltyAndRescheduleEnabledAlarms: ApplyPenaltyAndRescheduleEnabledAlarmsUseCase,
    private val getIncreasingVolumeEnabled: GetIncreasingVolumeEnabledUseCase,
    private val ringingAlarmStore: RingingAlarmStore,
    private val alarmSoundPlayer: AlarmSoundPlayer,
    private val alarmVibrator: AlarmVibrator,
    private val notificationBuilder: AlarmNotificationBuilder,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {
    companion object {
        const val NO_ALARM_ID = -1L
    }

    /**
     * Mirrors the ringing-alarm state into service behavior; suspends until cancelled.
     * A new ringing alarm cancels the in-flight ringing of the previous one.
     */
    suspend fun observeRingingAlarm(host: AlarmServiceHost) {
        var wasRinging = false
        ringingAlarmStore.ringingAlarmId.collectLatest { alarmId ->
            if (alarmId == null) {
                if (wasRinging) host.stop()
            } else {
                wasRinging = true
                ringAlarm(alarmId, host)
            }
        }
    }

    fun handleStartCommand(action: String?, alarmId: Long, host: AlarmServiceHost) {
        if (alarmId == NO_ALARM_ID) {
            host.stop()
            return
        }

        val previousAlarmId = ringingAlarmStore.ringingAlarmId.value

        if (action == AlarmService.ACTION_STOP_RINGING_FOR_ALARM) {
            if (alarmId == previousAlarmId) {
                ringingAlarmStore.clear()
            } else if (previousAlarmId == null) {
                host.stop()
            }
            return
        }

        ringingAlarmStore.setRingingAlarm(alarmId)
        if (previousAlarmId != null && previousAlarmId != alarmId) {
            applicationScope.launch { missAlarm(previousAlarmId) }
        }
    }

    private suspend fun ringAlarm(alarmId: Long, host: AlarmServiceHost) {
        val alarm = getAlarm(alarmId) ?: run {
            ringingAlarmStore.clear()
            return
        }
        val score = getScore()

        host.startInForeground(notificationBuilder.build(alarm, score))

        alarmVibrator.vibrate()
        alarmSoundPlayer.play(alarm.sound, gradualVolumeIncrease = getIncreasingVolumeEnabled())

        delay(Constants.Alarm.MISSED_TIMEOUT_MILLIS.milliseconds)
        applicationScope.launch {
            missAlarm(alarmId)
            applyPenaltyAndRescheduleEnabledAlarms()
        }
    }

    fun release() {
        ringingAlarmStore.clear()
        alarmVibrator.stop()
        alarmSoundPlayer.stop()
    }
}
