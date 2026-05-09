package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.GetTimeWithPenaltyOffsetUseCase
import javax.inject.Inject

class ScheduleAlarmUseCase @Inject constructor(
    private val scheduler: AlarmScheduler,
    private val timeCalculator: AlarmTimeCalculator,
    private val getTimeWithPenaltyOffset: GetTimeWithPenaltyOffsetUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val baseTime = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        val penalizedTime = getTimeWithPenaltyOffset(baseTime)
        val time = timeCalculator.pickSafeTriggerTime(penalizedTime, baseTime)
        scheduler.scheduleAlarm(alarm.id, time)
    }
}