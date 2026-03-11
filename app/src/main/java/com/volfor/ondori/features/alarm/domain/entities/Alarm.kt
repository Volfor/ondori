package com.volfor.ondori.features.alarm.domain.entities

import java.time.DayOfWeek

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val label: String? = null,
)