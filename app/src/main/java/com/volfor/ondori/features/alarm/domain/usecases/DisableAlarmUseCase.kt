package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import javax.inject.Inject

class DisableAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduler: AlarmScheduler,
) {
    suspend operator fun invoke(alarm: Alarm) {
        repo.disableAlarm(alarm.id)
        scheduler.cancelAlarm(alarm.id.toInt())
    }
}