package com.volfor.ondori.features.alarm.domain.usecases

import android.util.Log
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.scheduler.AlarmScheduler
import javax.inject.Inject

class CreateAlarmUseCase @Inject constructor(
    private val repo: AlarmRepository, private val scheduler: AlarmScheduler
) {
    suspend operator fun invoke(alarm: Alarm) {
        val id = repo.createAlarm(alarm)
        Log.d("AlarmManager", "Alarm created: $id")

        val time = System.currentTimeMillis() + 10_000 // TODO: get time from alarm
        scheduler.scheduleAlarm(id.toInt(), time)

    }
}