package com.volfor.ondori.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.volfor.ondori.features.alarm.domain.usecases.StartAlarmUseCase
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var startAlarm: StartAlarmUseCase

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(Constants.EXTRA_ALARM_ID, -1L)
        Log.d("AlarmManager", "Alarm fired: $alarmId")

        startAlarm(alarmId)
    }
}