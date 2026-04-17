package com.volfor.ondori.core.time

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.format.DateFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface TimeFormatStore {
    val is24Hour: StateFlow<Boolean>
}

@Singleton
class SystemTimeFormatStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : TimeFormatStore {
    private val _is24Hour = MutableStateFlow(DateFormat.is24HourFormat(context.applicationContext))
    override val is24Hour: StateFlow<Boolean> = _is24Hour.asStateFlow()

    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            _is24Hour.value = DateFormat.is24HourFormat(context.applicationContext)
        }
    }

    init {
        context.applicationContext.contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.TIME_12_24),
            false,
            observer,
        )
    }
}