package com.volfor.ondori.features.settings.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    companion object {
        const val DEFAULT_SNOOZE_MINUTES: Int = 5
        const val DEFAULT_INCREASING_VOLUME_ENABLED: Boolean = false
        val SNOOZE_MINUTES_RANGE: IntRange = 1..30
    }

    fun observeHasRequestedNotificationPermission(): Flow<Boolean>

    suspend fun setHasRequestedNotificationPermission(value: Boolean)

    fun observeSnoozeMinutes(): Flow<Int>

    suspend fun getSnoozeMinutes(): Int

    suspend fun setSnoozeMinutes(minutes: Int)

    fun observeIncreasingVolumeEnabled(): Flow<Boolean>

    suspend fun getIncreasingVolumeEnabled(): Boolean

    suspend fun setIncreasingVolumeEnabled(enabled: Boolean)
}