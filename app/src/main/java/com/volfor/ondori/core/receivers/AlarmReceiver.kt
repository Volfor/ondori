package com.volfor.ondori.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.volfor.ondori.core.AlarmService
import com.volfor.ondori.utils.Constants

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(Constants.EXTRA_ALARM_ID, -1L)
        Log.d("AlarmManager", "Alarm fired: $alarmId")

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        }

        ContextCompat.startForegroundService(context, serviceIntent)
    }
}