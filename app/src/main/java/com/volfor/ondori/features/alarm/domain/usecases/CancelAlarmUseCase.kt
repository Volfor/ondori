package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import javax.inject.Inject

class CancelAlarmUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler
) {
    operator fun invoke(alarmId: Long) {
        alarmScheduler.cancelAlarm(alarmId.toInt())
    }
}