package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.settings.domain.usecases.GetSnoozeMinutesUseCase
import javax.inject.Inject

class SnoozeAlarmUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val scheduler: AlarmScheduler,
    private val ringer: AlarmRinger,
    private val applyPenaltyAndRescheduleEnabledAlarms: ApplyPenaltyAndRescheduleEnabledAlarmsUseCase,
    private val getSnoozeMinutes: GetSnoozeMinutesUseCase
) {
    suspend operator fun invoke(alarmId: Long) {
        ringer.stopRinging(alarmId)
        applyPenaltyAndRescheduleEnabledAlarms()
        val time = timeCalculator.computeSnoozeTriggerTime(getSnoozeMinutes())
        scheduler.scheduleAlarm(alarmId, time)
    }
}