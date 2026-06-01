package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class EnableAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val checkDismissReversalForAlarm: CheckDismissReversalForAlarmUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarmId: Long) {
        val alarm = repo.getAlarm(alarmId) ?: return
        repo.enableAlarm(alarm.id)
        if (checkDismissReversalForAlarm(alarm)) {
            rescheduleEnabledAlarms()
        }
        scheduleAlarm(alarm)
    }
}