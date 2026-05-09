package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.MissedAlarmNotifier
import com.volfor.ondori.features.punisher.domain.usecases.ApplyPenaltyUseCase
import javax.inject.Inject

class MissAlarmUseCase @Inject constructor(
    private val ringer: AlarmRinger,
    private val repo: AlarmRepository,
    private val missedNotifier: MissedAlarmNotifier,
    private val applyPenalty: ApplyPenaltyUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke(alarmId: Long) {
        ringer.stopRinging(alarmId)
        val alarm = repo.getAlarm(alarmId) ?: return
        missedNotifier.notifyMissed(alarm)
        applyPenalty()
        rescheduleEnabledAlarms()
    }
}
