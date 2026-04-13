package com.volfor.ondori.features.prefs.domain.repositories

import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {

    fun observeHasRequestedNotificationPermission(): Flow<Boolean>

    suspend fun setHasRequestedNotificationPermission(value: Boolean)
}