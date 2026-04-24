package com.volfor.ondori.features.punisher.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val score: Int = 0,
) {
    companion object {
        const val SINGLETON_ID = 1
    }
}