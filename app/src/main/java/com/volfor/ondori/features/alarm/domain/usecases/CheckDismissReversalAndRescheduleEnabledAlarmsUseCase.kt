package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.DetectDismissReversalUseCase
import javax.inject.Inject

class CheckDismissReversalAndRescheduleEnabledAlarmsUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val detectDismissReversal: DetectDismissReversalUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val triggerTime = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        if (detectDismissReversal(triggerTime)) {
            rescheduleEnabledAlarms()
        }
    }
}