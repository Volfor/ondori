package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val scheduler: AlarmScheduler
) {
    operator fun invoke(alarmId: Long, triggerAtMillis: Long) {
        scheduler.scheduleAlarm(alarmId, triggerAtMillis)
    }
}