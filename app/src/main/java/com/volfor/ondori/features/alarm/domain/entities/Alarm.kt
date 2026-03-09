package com.volfor.ondori.features.alarm.domain.entities

data class Alarm(
    val id: Int,
    val label: String,
    val enabled: Boolean,
)