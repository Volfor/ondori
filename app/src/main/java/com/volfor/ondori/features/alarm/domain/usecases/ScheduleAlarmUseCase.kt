package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    operator fun invoke(alarmId: Long, triggerAtMillis: Long) {
        alarmScheduler.scheduleAlarm(alarmId.toInt(), triggerAtMillis)
    }
}