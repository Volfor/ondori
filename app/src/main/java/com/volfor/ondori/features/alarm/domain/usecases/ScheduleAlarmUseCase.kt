package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val scheduler: AlarmScheduler,
    private val timeCalculator: AlarmTimeCalculator,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val time = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        scheduler.scheduleAlarm(alarm.id, time)
    }
}