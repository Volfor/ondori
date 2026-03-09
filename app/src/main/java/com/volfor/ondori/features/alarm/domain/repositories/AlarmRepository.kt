package com.volfor.ondori.features.alarm.domain.repositories

import com.volfor.ondori.features.alarm.domain.entities.Alarm

interface AlarmRepository {
    fun getAlarms(): List<Alarm>
}