package com.volfor.ondori.core

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.volfor.ondori.R
import com.volfor.ondori.app.FIRING_ALARMS_CHANNEL_ID
import com.volfor.ondori.features.alarm.presentation.activities.AlarmRingingActivity

class AlarmService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AlarmService", "Alarm service started")
        val alarmId = intent?.getLongExtra("alarm_id", -1L) ?: -1L

        if (alarmId == -1L) {
            Log.w("AlarmService", "Started without alarm_id, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        val activityIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra("alarm_id", alarmId)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, FIRING_ALARMS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm")
            .setContentText("Wake up")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(R.drawable.ic_launcher_foreground, "Snooze", null)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", null)
            .build()

        //TODO:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED,
            )
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}