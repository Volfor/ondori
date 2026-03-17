package com.volfor.ondori.features.alarm.data

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.volfor.ondori.core.AlarmService
import com.volfor.ondori.features.alarm.domain.services.AlarmRingingController
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmRingingControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmRingingController {

    override fun startRinging(alarmId: Long) {
        val serviceIntent = Intent(context.applicationContext, AlarmService::class.java).apply {
            putExtra("alarm_id", alarmId)
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