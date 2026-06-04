package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class EnableAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
) {
    suspend operator fun invoke(alarmId: Long): Boolean {
        val alarm = repo.getAlarm(alarmId) ?: return false
        repo.enableAlarm(alarm.id)
        scheduleAlarm(alarm)
        return true
    }
}