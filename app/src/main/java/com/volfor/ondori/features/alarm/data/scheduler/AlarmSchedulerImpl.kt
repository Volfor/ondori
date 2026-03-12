package com.volfor.ondori.features.alarm.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.volfor.ondori.core.AlarmReceiver
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val ALARM_TRIGGER_SAFETY_OFFSET_MS = 500

class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context, private val alarmManager: AlarmManager
) : AlarmScheduler {

    override fun scheduleAlarm(alarmId: Int, triggerAtMillis: Long) {
        val pendingIntent = createPendingIntent(alarmId)
        val info = AlarmManager.AlarmClockInfo(
            triggerAtMillis + ALARM_TRIGGER_SAFETY_OFFSET_MS,
            pendingIntent
        )
        alarmManager.setAlarmClock(info, pendingIntent)
    }

    override fun cancelAlarm(alarmId: Int) {
        Log.d("AlarmManager", "Alarm canceled: $alarmId")
        val pendingIntent = createPendingIntent(alarmId)
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(alarmId: Int): PendingIntent {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_id", alarmId)
        }

        return PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}