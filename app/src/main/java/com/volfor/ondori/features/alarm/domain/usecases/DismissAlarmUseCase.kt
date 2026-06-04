package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import javax.inject.Inject

class DismissAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val ringer: AlarmRinger,
    private val applyRewardAndRescheduleEnabledAlarms: ApplyRewardAndRescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarmId: Long) {
        ringer.stopRinging(alarmId)
        val alarm = repo.getAlarm(alarmId) ?: return
        if (alarm.repeatDays.isEmpty()) {
            repo.disableAlarm(alarmId)
        }
        applyRewardAndRescheduleEnabledAlarms()
    }
}