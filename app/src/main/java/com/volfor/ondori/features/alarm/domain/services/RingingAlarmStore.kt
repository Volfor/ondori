package com.volfor.ondori.features.alarm.domain.services

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface RingingAlarmStore {
    val ringingAlarmId: StateFlow<Long?>
    val stoppedAlarmId: SharedFlow<Long>
    fun setRingingAlarm(alarmId: Long)
    fun clear()
}