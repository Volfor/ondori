package com.volfor.ondori.features.alarm.domain.repositories

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun getAlarmsStream(): Flow<List<Alarm>>

    suspend fun createAlarm(alarm: Alarm): Long

    suspend fun enableAlarm(alarmId: Long)

    suspend fun disableAlarm(alarmId: Long)
}