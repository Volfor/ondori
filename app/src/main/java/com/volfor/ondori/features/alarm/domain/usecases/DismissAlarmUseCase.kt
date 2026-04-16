package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val ringer: AlarmRinger,
) {
    suspend operator fun invoke(alarmId: Long) {
        try {
            val alarm = repo.getAlarm(alarmId) ?: return
            if (alarm.repeatDays.isNotEmpty()) {
                scheduleAlarm(alarm)
            } else {
                repo.disableAlarm(alarmId)
            }
        } finally {
            ringer.stopRinging(alarmId)
        }
    }
}