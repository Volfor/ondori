package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class UpdateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val cancelAlarm: CancelAlarmUseCase,
    private val checkDismissReversalForAlarm: CheckDismissReversalForAlarmUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val previous = repo.getAlarm(alarm.id)
        repo.updateAlarm(alarm)
        if (alarm.enabled) {
            if (previous != null && isTimeChanged(previous, alarm)) {
                if (checkDismissReversalForAlarm(alarm)) {
                    rescheduleEnabledAlarms()
                }
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