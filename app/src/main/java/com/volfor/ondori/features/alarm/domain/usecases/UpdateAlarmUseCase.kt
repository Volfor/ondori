package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class UpdateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val disableAlarm: DisableAlarmUseCase,
) {
    suspend operator fun invoke(alarm: Alarm) {
        repo.updateAlarm(alarm)
        if (alarm.enabled) {
            scheduleAlarm(alarm)
        } else {
            disableAlarm(alarm.id)
        }
    }
}