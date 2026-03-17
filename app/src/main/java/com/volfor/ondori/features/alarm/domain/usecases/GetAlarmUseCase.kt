package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class GetAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository
) {
    suspend operator fun invoke(alarmId: Long): Alarm? {
        return repo.getAlarm(alarmId)
    }
}