package com.volfor.ondori.features.alarm.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

enum class AlarmSoundMode {
    DEFAULT, SILENT, CUSTOM,
}

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val repeatDays: Set<DayOfWeek>,
    val enabled: Boolean,
    val label: String?,
    val soundMode: AlarmSoundMode,
    val soundUri: String?,
)