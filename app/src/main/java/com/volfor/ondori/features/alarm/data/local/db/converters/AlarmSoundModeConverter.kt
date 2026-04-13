package com.volfor.ondori.features.alarm.data.local.db.converters

import androidx.room.TypeConverter
import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmSoundMode

class AlarmSoundModeConverter {
    @TypeConverter
    fun fromEnum(mode: AlarmSoundMode): String = mode.name

    @TypeConverter
    fun toEnum(value: String): AlarmSoundMode =
        runCatching { AlarmSoundMode.valueOf(value) }.getOrDefault(
            AlarmSoundMode.DEFAULT
        )
}