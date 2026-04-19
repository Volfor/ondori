package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import javax.inject.Inject

class StartAlarmUseCase @Inject constructor(
    private val ringer: AlarmRinger,
) {
    operator fun invoke(alarmId: Long) {
        ringer.startRinging(alarmId)
    }
}