package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRingingController
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import javax.inject.Inject

class SnoozeAlarmUseCase @Inject constructor(
    private val timeCalculator: AlarmTimeCalculator,
    private val scheduler: AlarmScheduler,
    private val ringingController: AlarmRingingController,
) {
    operator fun invoke(alarmId: Long) {
        val time = timeCalculator.computeSnoozeTriggerTime()
        scheduler.scheduleAlarm(alarmId, time)
        ringingController.stopRinging()
    }
}