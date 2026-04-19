package com.volfor.ondori.features.alarm.domain.services

import java.time.DayOfWeek

interface AlarmTimeCalculator {

    fun computeNextTriggerTime(hour: Int, minute: Int, repeatDays: Set<DayOfWeek>): Long

    fun computeSnoozeTriggerTime(): Long
}