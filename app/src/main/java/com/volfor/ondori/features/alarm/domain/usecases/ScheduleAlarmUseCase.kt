package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.GetPenaltyOffsetMillisUseCase
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val scheduler: AlarmScheduler,
    private val timeCalculator: AlarmTimeCalculator,
    private val getPenaltyOffsetMillis: GetPenaltyOffsetMillisUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        var time = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        time -= getPenaltyOffsetMillis()
        scheduler.scheduleAlarm(alarm.id, time)
    }
}