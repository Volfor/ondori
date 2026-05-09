package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.ApplyPenaltyUseCase
import javax.inject.Inject

class SnoozeAlarmUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val scheduler: AlarmScheduler,
    private val ringer: AlarmRinger,
    private val applyPenalty: ApplyPenaltyUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarmId: Long) {
        ringer.stopRinging(alarmId)
        applyPenalty()
        rescheduleEnabledAlarms()
        val time = timeCalculator.computeSnoozeTriggerTime()
        scheduler.scheduleAlarm(alarmId, time)
    }
}