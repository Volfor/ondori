package com.volfor.ondori.features.alarm.domain

interface AlarmScheduler {
    fun scheduleAlarm(alarmId: Int, triggerAtMillis: Long)
    fun cancelAlarm(alarmId: Int)
}
