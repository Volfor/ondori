package com.volfor.ondori.features.alarm.domain.services

interface AlarmScheduler {
    fun scheduleAlarm(alarmId: Long, triggerAtMillis: Long)
    fun cancelAlarm(alarmId: Long)
}