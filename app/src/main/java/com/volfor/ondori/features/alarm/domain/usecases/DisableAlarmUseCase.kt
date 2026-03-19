package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import javax.inject.Inject

class DisableAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduler: AlarmScheduler,
    private val ringer: AlarmRinger,
) {
    suspend operator fun invoke(alarmId: Long) {
        ringer.stopRinging()
        scheduler.cancelAlarm(alarmId)
        repo.disableAlarm(alarmId)
    }
}