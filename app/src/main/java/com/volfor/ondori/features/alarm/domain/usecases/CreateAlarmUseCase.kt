package com.volfor.ondori.features.alarm.domain.usecases

import android.util.Log
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import javax.inject.Inject

class CreateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository,
    private val timeCalculator: AlarmTimeCalculator,
    private val scheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm) {
        val id = repo.createAlarm(alarm)
        Log.d("AlarmManager", "Alarm created: $id")

        val time = timeCalculator.computeNextTriggerTime(hour = alarm.hour, minute = alarm.minute)
        scheduler.scheduleAlarm(id.toInt(), time)
    }
}