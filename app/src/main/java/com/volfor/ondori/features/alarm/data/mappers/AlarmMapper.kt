package com.volfor.ondori.features.alarm.data.mappers

import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmEntity
import com.volfor.ondori.features.alarm.domain.entities.Alarm

fun AlarmEntity.toDomain(): Alarm {
    return Alarm(
        id = id,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
        enabled = enabled,
        label = label,
    )
}

@JvmName("localToDomain")
fun List<AlarmEntity>.toDomain() = map(AlarmEntity::toDomain)

fun Alarm.toLocal(): AlarmEntity {
    return AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
        enabled = enabled,
        label = label,
    )
}