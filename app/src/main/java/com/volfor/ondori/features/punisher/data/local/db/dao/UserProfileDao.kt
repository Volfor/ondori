package com.volfor.ondori.features.punisher.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.volfor.ondori.features.punisher.data.local.db.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = ${UserProfileEntity.SINGLETON_ID}")
    fun observeUserProfile(): Flow<UserProfileEntity>

    @Query("SELECT * FROM user_profile WHERE id = ${UserProfileEntity.SINGLETON_ID}")
    suspend fun getUserProfile(): UserProfileEntity

    @Query("SELECT score FROM user_profile WHERE id = ${UserProfileEntity.SINGLETON_ID}")
    fun observeScore(): Flow<Int>

    @Query("SELECT score FROM user_profile WHERE id = ${UserProfileEntity.SINGLETON_ID}")
    suspend fun getScore(): Int

    @Query("UPDATE user_profile SET score = :score WHERE id = ${UserProfileEntity.SINGLETON_ID}")
    suspend fun updateScore(score: Int)
}