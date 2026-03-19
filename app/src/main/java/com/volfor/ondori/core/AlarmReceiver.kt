package com.volfor.ondori.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        Log.d("AlarmManager", "Alarm fired: $alarmId")

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
        }

        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
