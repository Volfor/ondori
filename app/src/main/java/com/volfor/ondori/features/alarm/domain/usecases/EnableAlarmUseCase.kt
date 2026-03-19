package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import javax.inject.Inject

class EnableAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduler: AlarmScheduler,
    private val timeCalculator: AlarmTimeCalculator,
) {
    suspend operator fun invoke(alarmId: Long) {
        val alarm = repo.getAlarm(alarmId) ?: return
        repo.enableAlarm(alarmId)

        val time = timeCalculator.computeNextTriggerTime(hour = alarm.hour, minute = alarm.minute)
        scheduler.scheduleAlarm(alarmId, time)
    }
}