package com.volfor.ondori.features.alarm.domain.services

import com.volfor.ondori.features.alarm.domain.entities.Alarm

interface MissedAlarmNotifier {

    fun notifyMissed(alarm: Alarm)
}
