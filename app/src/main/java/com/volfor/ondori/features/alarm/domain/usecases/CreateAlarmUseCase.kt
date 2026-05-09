package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.DetectDismissReversalUseCase
import javax.inject.Inject

class CreateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val timeCalculator: AlarmTimeCalculator,
    private val detectDismissReversal: DetectDismissReversalUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val id = repo.createAlarm(alarm)
        val triggerTime = timeCalculator.computeNextTriggerTime(
            hour = alarm.hour,
            minute = alarm.minute,
            repeatDays = alarm.repeatDays,
        )
        detectDismissReversal(triggerTime)
        scheduleAlarm(alarm.copy(id = id))
    }
}