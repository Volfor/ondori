package com.volfor.ondori.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val APP_PREFS_NAME = "app_preferences"

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFS_NAME)

object AppPreferencesKeys {

    val HAS_REQUESTED_NOTIFICATION_PERMISSION =
        booleanPreferencesKey("has_requested_notification_permission")
}