package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val scheduler: AlarmScheduler,
    private val computeScheduledTriggerTime: ComputeAlarmTriggerTimeUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val time = computeScheduledTriggerTime(alarm)
        scheduler.scheduleAlarm(alarm.id, time)
    }
}