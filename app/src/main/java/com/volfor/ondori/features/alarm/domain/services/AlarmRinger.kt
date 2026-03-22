package com.volfor.ondori.features.alarm.domain.services

interface AlarmRinger {

    fun startRinging(alarmId: Long)

    fun stopRinging(alarmId: Long)
}