package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import javax.inject.Inject

class ComputeTimeUntilAlarmUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        val baseTime = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        return timeCalculator.computeTimeUntilTrigger(baseTime)
    }
}
