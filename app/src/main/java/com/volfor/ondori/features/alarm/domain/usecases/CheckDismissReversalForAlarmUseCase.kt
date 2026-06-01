package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.DetectDismissReversalUseCase
import javax.inject.Inject

class CheckDismissReversalForAlarmUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val detectDismissReversal: DetectDismissReversalUseCase,
) {
    suspend operator fun invoke(alarm: Alarm): Boolean {
        val triggerTime = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        return detectDismissReversal(triggerTime)
    }
}