package com.volfor.ondori.features.alarm.domain.repositories

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun observeAlarms(): Flow<List<Alarm>>

    suspend fun getAlarm(alarmId: Long): Alarm?

    suspend fun createAlarm(alarm: Alarm): Long

    suspend fun updateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarmId: Long)

    suspend fun enableAlarm(alarmId: Long)

    suspend fun disableAlarm(alarmId: Long)
}