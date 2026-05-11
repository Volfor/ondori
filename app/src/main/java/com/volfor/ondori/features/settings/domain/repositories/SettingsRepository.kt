package com.volfor.ondori.features.settings.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    companion object {
        const val DEFAULT_SNOOZE_MINUTES: Int = 5
        val SNOOZE_MINUTES_RANGE: IntRange = 1..30
    }

    fun observeHasRequestedNotificationPermission(): Flow<Boolean>

    suspend fun setHasRequestedNotificationPermission(value: Boolean)

    fun observeSnoozeMinutes(): Flow<Int>

    suspend fun getSnoozeMinutes(): Int

    suspend fun setSnoozeMinutes(minutes: Int)
}