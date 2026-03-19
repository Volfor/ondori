package com.volfor.ondori.features.alarm.presentation.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.volfor.ondori.features.alarm.presentation.screens.AlarmRingingScreen
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        Log.d("AlarmRingingActivity", "Alarm ringing: $alarmId")

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        //TODO:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
        }

        //TODO:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true)
        }

        setContent {
            AlarmRingingScreen(
                onAlarmHandled = {
                    finish()
                },
            )
        }
    }
}