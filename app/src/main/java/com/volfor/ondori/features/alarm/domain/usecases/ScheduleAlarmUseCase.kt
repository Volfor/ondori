package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.scheduler.AlarmScheduler
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    operator fun invoke(alarmId: Long, triggerAtMillis: Long) {
        alarmScheduler.scheduleAlarm(alarmId.toInt(), triggerAtMillis)
    }
}