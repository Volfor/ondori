package com.volfor.ondori.features.alarm.data.local.db.converters

import androidx.room.TypeConverter
import java.time.DayOfWeek

class RepeatDaysConverter {
    @TypeConverter
    fun fromDays(days: Set<DayOfWeek>): Int {
        return days.fold(0) { acc, day ->
            acc or (1 shl (day.value - 1))
        }
    }

    @TypeConverter
    fun toDays(mask: Int): Set<DayOfWeek> {
        return DayOfWeek.entries.filter {
            mask and (1 shl (it.value - 1)) != 0
        }.toSet()
    }
}