package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class DisableAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val cancelAlarm: CancelAlarmUseCase,
) {
    suspend operator fun invoke(alarmId: Long) {
        cancelAlarm(alarmId)
        repo.disableAlarm(alarmId)
    }
}