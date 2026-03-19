package com.volfor.ondori.features.alarm.data.ringer

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.volfor.ondori.core.AlarmService
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmRingerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmRinger {

    override fun startRinging(alarmId: Long) {
        val serviceIntent = Intent(context.applicationContext, AlarmService::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
        }

        ContextCompat.startForegroundService(context.applicationContext, serviceIntent)
    }

    override fun stopRinging() {
        context.applicationContext.stopService(
            Intent(
                context.applicationContext,
                AlarmService::class.java,
            )
        )
    }
}