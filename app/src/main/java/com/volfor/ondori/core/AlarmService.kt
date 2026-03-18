package com.volfor.ondori.core

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.volfor.ondori.R
import com.volfor.ondori.app.FIRING_ALARMS_CHANNEL_ID
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import com.volfor.ondori.features.alarm.presentation.activities.AlarmRingingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService : LifecycleService() {

    companion object {
        const val ACTION_SNOOZE = "com.volfor.ondori.ACTION_SNOOZE"
        const val ACTION_STOP = "com.volfor.ondori.ACTION_STOP"
        const val ACTION_DISMISS = "com.volfor.ondori.ACTION_DISMISS"

        private const val SNOOZE_REQUEST_CODE = 1001
        private const val STOP_REQUEST_CODE = 1002
        private const val DISMISS_REQUEST_CODE = 1003
    }

    @Inject
    lateinit var snoozeAlarm: SnoozeAlarmUseCase

    @Inject
    lateinit var dismissAlarm: DismissAlarmUseCase

    @Inject
    lateinit var alarmSoundPlayer: AlarmSoundPlayer

    @Inject
    lateinit var alarmVibrator: AlarmVibrator

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.d("AlarmService", "Alarm service started")
        val alarmId = intent?.getLongExtra("alarm_id", -1L) ?: -1L

        if (alarmId == -1L) {
            Log.w("AlarmService", "Started without alarm_id, stopping")
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent?.action) {
            ACTION_SNOOZE -> {
                lifecycleScope.launch {
                    snoozeAlarm(alarmId)
                }
                return START_NOT_STICKY
            }

            ACTION_DISMISS, ACTION_STOP -> {
                lifecycleScope.launch {
                    dismissAlarm(alarmId)
                }
                return START_NOT_STICKY
            }
        }

        val notification = buildAlarmNotification(alarmId)

        //TODO:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED,
            )
        }

        alarmVibrator.vibrate()
        alarmSoundPlayer.play(R.raw.rooster)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        alarmVibrator.stop()
        alarmSoundPlayer.stop()
        super.onDestroy()
    }

    private fun buildAlarmNotification(alarmId: Long): Notification {
        val activityIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra("alarm_id", alarmId)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, FIRING_ALARMS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm")
            .setContentText("Wake up")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Snooze",
                createSnoozePendingIntent(alarmId),
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                createStopPendingIntent(alarmId),
            )
            .setDeleteIntent(createDeletePendingIntent(alarmId))
            .build()
    }

    private fun createSnoozePendingIntent(alarmId: Long): PendingIntent {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_SNOOZE
            putExtra("alarm_id", alarmId)
        }
        return PendingIntent.getService(
            this,
            SNOOZE_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createStopPendingIntent(alarmId: Long): PendingIntent {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP
            putExtra("alarm_id", alarmId)
        }
        return PendingIntent.getService(
            this,
            STOP_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createDeletePendingIntent(alarmId: Long): PendingIntent {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_DISMISS
            putExtra("alarm_id", alarmId)
        }
        return PendingIntent.getService(
            this,
            DISMISS_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}