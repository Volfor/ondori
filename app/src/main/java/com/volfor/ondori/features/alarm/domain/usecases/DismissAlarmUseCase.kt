package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRingingController
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val ringingController: AlarmRingingController,
) {
    suspend operator fun invoke(alarmId: Long) {
        repo.disableAlarm(alarmId)
        ringingController.stopRinging()
    }
}