package com.volfor.ondori.features.alarm.domain.services

interface AlarmRingingController {

    fun startRinging(alarmId: Long)

    fun stopRinging()
}