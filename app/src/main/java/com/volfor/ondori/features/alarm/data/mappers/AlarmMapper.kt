package com.volfor.ondori.features.alarm.data.mappers

import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmEntity
import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmSoundMode
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound

fun AlarmEntity.toDomain(): Alarm {
    return Alarm(
        id = id,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
        enabled = enabled,
        label = label,
        sound = soundMode.toDomainSound(soundUri)
    )
}

@JvmName("localToDomain")
fun List<AlarmEntity>.toDomain() = map(AlarmEntity::toDomain)

fun Alarm.toLocal(): AlarmEntity {
    val (soundMode, soundUri) = sound.toLocal()
    return AlarmEntity(
        id = id,
        hour = hour,
        minute = minute,
        repeatDays = repeatDays,
        enabled = enabled,
        label = label,
        soundMode = soundMode,
        soundUri = soundUri,
    )
}

private fun AlarmSoundMode.toDomainSound(soundUri: String?): AlarmSound = when (this) {
    AlarmSoundMode.DEFAULT -> AlarmSound.Default
    AlarmSoundMode.SILENT -> AlarmSound.Silent
    AlarmSoundMode.CUSTOM -> if (!soundUri.isNullOrBlank()) {
        AlarmSound.Custom(soundUri)
    } else {
        AlarmSound.Default
    }
}

private fun AlarmSound.toLocal(): Pair<AlarmSoundMode, String?> = when (this) {
    AlarmSound.Default -> AlarmSoundMode.DEFAULT to null
    AlarmSound.Silent -> AlarmSoundMode.SILENT to null
    is AlarmSound.Custom -> AlarmSoundMode.CUSTOM to uri
}