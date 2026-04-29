package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.DetectRescheduledAlarmUseCase
import javax.inject.Inject

class UpdateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val cancelAlarm: CancelAlarmUseCase,
    private val timeCalculator: AlarmTimeCalculator,
    private val detectRescheduledAlarm: DetectRescheduledAlarmUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val previous = repo.getAlarm(alarm.id)
        repo.updateAlarm(alarm)
        if (alarm.enabled) {
            if (previous != null && isTimeChanged(previous, alarm)) {
                val triggerTime = timeCalculator.computeNextTriggerTime(
                    hour = alarm.hour,
                    minute = alarm.minute,
                    repeatDays = alarm.repeatDays,
                )
                detectRescheduledAlarm(triggerTime)
            }
            scheduleAlarm(alarm)
        } else {
            cancelAlarm(alarm.id)
        }
    }

    private fun isTimeChanged(previous: Alarm, updated: Alarm): Boolean {
        return previous.hour != updated.hour || previous.minute != updated.minute || previous.repeatDays != updated.repeatDays
    }
}