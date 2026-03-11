package com.volfor.ondori.features.alarm.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek

@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val repeatDays: Set<DayOfWeek>,
    val enabled: Boolean,
    val label: String?,
)