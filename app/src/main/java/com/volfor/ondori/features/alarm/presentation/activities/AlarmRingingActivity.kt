package com.volfor.ondori.features.alarm.presentation.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.features.alarm.presentation.screens.AlarmRingingScreen
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        Log.d("AlarmRingingActivity", "Alarm ringing: $alarmId")

        configureShowOverLockScreenAndWakeScreen()
        setContent {
            OndoriTheme {
                AlarmRingingScreen(
                    onAlarmHandled = {
                        finish()
                    },
                )
            }
        }
    }

    private fun configureShowOverLockScreenAndWakeScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION") window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}