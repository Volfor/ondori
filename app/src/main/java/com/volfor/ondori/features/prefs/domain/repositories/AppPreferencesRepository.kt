package com.volfor.ondori.features.prefs.domain.repositories

import kotlinx.coroutines.flow.Flow

interface AppPreferencesRepository {
    val notifPermissionPromptShownFlow: Flow<Boolean>
    suspend fun setNotifPermissionPromptShown(shown: Boolean)
}