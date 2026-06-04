package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class CreateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val checkDismissReversalAndRescheduleEnabledAlarms: CheckDismissReversalAndRescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        val id = repo.createAlarm(alarm)
        checkDismissReversalAndRescheduleEnabledAlarms(alarm)
        scheduleAlarm(alarm.copy(id = id))
    }
}