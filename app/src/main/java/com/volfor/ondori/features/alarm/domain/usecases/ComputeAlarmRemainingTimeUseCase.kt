package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import javax.inject.Inject

class ComputeAlarmRemainingTimeUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val computeAlarmTriggerTime: ComputeAlarmTriggerTimeUseCase,
) {
    suspend operator fun invoke(alarm: Alarm): Long {
        val triggerTime = computeAlarmTriggerTime(alarm)
        return timeCalculator.computeRemainingTime(triggerTime)
    }
}
