package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.GetTimeWithPenaltyOffsetUseCase
import javax.inject.Inject

class ComputeAlarmTriggerTimeUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val getTimeWithPenaltyOffset: GetTimeWithPenaltyOffsetUseCase,
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        val baseTime = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        val penalizedTime = getTimeWithPenaltyOffset(baseTime)
        return timeCalculator.pickSafeTriggerTime(penalizedTime, baseTime)
    }
}
