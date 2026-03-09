package com.volfor.ondori.features.alarm.data.models

import com.volfor.ondori.features.alarm.domain.entities.Alarm

fun AlarmModel.toDomain(): Alarm {
    return Alarm(
        id = id,
        label = label,
        enabled = enabled,
    )
}

data class AlarmModel(
    val id: Int, val label: String, val enabled: Boolean
) {

}