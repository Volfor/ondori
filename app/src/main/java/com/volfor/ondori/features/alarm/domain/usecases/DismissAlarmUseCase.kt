package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.punisher.domain.usecases.RecordCleanDismissUseCase
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val ringer: AlarmRinger,
    private val recordCleanDismiss: RecordCleanDismissUseCase,
) {
    suspend operator fun invoke(alarmId: Long) {
        ringer.stopRinging(alarmId)
        val alarm = repo.getAlarm(alarmId) ?: return
        recordCleanDismiss()
        if (alarm.repeatDays.isNotEmpty()) {
            scheduleAlarm(alarm)
        } else {
            repo.disableAlarm(alarmId)
        }
    }
}