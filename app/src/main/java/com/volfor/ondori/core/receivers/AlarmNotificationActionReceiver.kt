package com.volfor.ondori.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.volfor.ondori.di.ApplicationScope
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmNotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_NOTIFICATION_SNOOZE = "com.volfor.ondori.ACTION_NOTIFICATION_SNOOZE"
        const val ACTION_NOTIFICATION_STOP = "com.volfor.ondori.ACTION_NOTIFICATION_STOP"
        const val ACTION_NOTIFICATION_DISMISS = "com.volfor.ondori.ACTION_NOTIFICATION_DISMISS"
    }

    @Inject
    lateinit var dismissAlarm: DismissAlarmUseCase

    @Inject
    lateinit var snoozeAlarm: SnoozeAlarmUseCase

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(Constants.EXTRA_ALARM_ID, -1L)
        if (alarmId == -1L) return

        val pendingResult = goAsync()
        applicationScope.launch {
            try {
                when (intent.action) {
                    ACTION_NOTIFICATION_SNOOZE -> snoozeAlarm(alarmId)
                    ACTION_NOTIFICATION_STOP, ACTION_NOTIFICATION_DISMISS -> dismissAlarm(alarmId)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}