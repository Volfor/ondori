package com.volfor.ondori.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.volfor.ondori.features.alarm.data.local.db.converters.AlarmSoundModeConverter
import com.volfor.ondori.features.alarm.data.local.db.converters.RepeatDaysConverter
import com.volfor.ondori.features.alarm.data.local.db.dao.AlarmDao
import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmEntity
import com.volfor.ondori.features.punisher.data.local.db.dao.UserProfileDao
import com.volfor.ondori.features.punisher.data.local.db.entities.UserProfileEntity

@Database(entities = [AlarmEntity::class, UserProfileEntity::class], version = 1)
@TypeConverters(RepeatDaysConverter::class, AlarmSoundModeConverter::class)
abstract class OndoriDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    abstract fun userProfileDao(): UserProfileDao
}