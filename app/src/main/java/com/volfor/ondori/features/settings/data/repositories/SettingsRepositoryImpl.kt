package com.volfor.ondori.features.settings.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.volfor.ondori.data.local.datastore.AppPreferencesKeys.HAS_REQUESTED_NOTIFICATION_PERMISSION
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun observeHasRequestedNotificationPermission(): Flow<Boolean> {
        return dataStore.data.map { it[HAS_REQUESTED_NOTIFICATION_PERMISSION] ?: false }
    }

    override suspend fun setHasRequestedNotificationPermission(value: Boolean) {
        dataStore.edit { it[HAS_REQUESTED_NOTIFICATION_PERMISSION] = value }
    }
}