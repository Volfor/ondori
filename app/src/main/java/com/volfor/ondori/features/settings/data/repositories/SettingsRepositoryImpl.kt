package com.volfor.ondori.features.settings.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.volfor.ondori.data.local.datastore.AppPreferencesKeys.HAS_REQUESTED_NOTIFICATION_PERMISSION
import com.volfor.ondori.data.local.datastore.AppPreferencesKeys.SNOOZE_DURATION_MINUTES
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository.Companion.DEFAULT_SNOOZE_MINUTES
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository.Companion.SNOOZE_MINUTES_RANGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override fun observeSnoozeMinutes(): Flow<Int> {
        return dataStore.data.map {
            (it[SNOOZE_DURATION_MINUTES] ?: DEFAULT_SNOOZE_MINUTES).coerceIn(SNOOZE_MINUTES_RANGE)
        }
    }

    override suspend fun getSnoozeMinutes(): Int {
        return observeSnoozeMinutes().first()
    }

    override suspend fun setSnoozeMinutes(minutes: Int) {
        dataStore.edit { it[SNOOZE_DURATION_MINUTES] = minutes.coerceIn(SNOOZE_MINUTES_RANGE) }
    }
}