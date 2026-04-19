package com.volfor.ondori.features.settings.domain.repositories

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun observeHasRequestedNotificationPermission(): Flow<Boolean>

    suspend fun setHasRequestedNotificationPermission(value: Boolean)
}