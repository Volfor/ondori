package com.volfor.ondori.features.prefs.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.volfor.ondori.data.local.datastore.AppPreferencesKeys.NOTIFICATION_PERMISSION_PROMPT_SHOWN
import com.volfor.ondori.features.prefs.domain.repositories.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppPreferencesRepository {

    override val notifPermissionPromptShownFlow: Flow<Boolean> =
        dataStore.data.map { it[NOTIFICATION_PERMISSION_PROMPT_SHOWN] ?: false }


    override suspend fun setNotifPermissionPromptShown(shown: Boolean) {
        dataStore.edit { it[NOTIFICATION_PERMISSION_PROMPT_SHOWN] = shown }
    }
}