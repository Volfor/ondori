package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.AlarmScheduler
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    operator fun invoke(alarmId: Int, triggerAtMillis: Long) {
        alarmScheduler.scheduleAlarm(alarmId, triggerAtMillis)
    }
}