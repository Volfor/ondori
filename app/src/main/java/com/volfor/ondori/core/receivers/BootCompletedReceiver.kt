package com.volfor.ondori.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.volfor.ondori.di.ApplicationScope
import com.volfor.ondori.features.alarm.domain.usecases.RescheduleEnabledAlarmsUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        Log.d("BootCompletedReceiver", "Device booted; rescheduling alarms")

        val pendingResult = goAsync()
        applicationScope.launch {
            try {
                rescheduleEnabledAlarms()
            } finally {
                pendingResult.finish()
            }
        }
    }
}