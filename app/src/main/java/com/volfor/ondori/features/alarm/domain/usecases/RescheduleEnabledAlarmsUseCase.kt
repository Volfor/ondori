package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class RescheduleEnabledAlarmsUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
) {
    suspend operator fun invoke() {
        val enabledAlarms = repo.getEnabledAlarms()
        for (alarm in enabledAlarms) {
            scheduleAlarm(alarm)
        }
    }
}