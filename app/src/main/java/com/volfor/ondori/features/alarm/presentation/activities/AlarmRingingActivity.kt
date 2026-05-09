package com.volfor.ondori.features.alarm.presentation.activities

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.app.theme.OndoriTheme
import com.volfor.ondori.app.time.LocalIs24HourFormat
import com.volfor.ondori.app.time.TimeFormatStore
import com.volfor.ondori.features.alarm.presentation.screens.AlarmRingingScreen
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    @Inject
    lateinit var timeFormatStore: TimeFormatStore

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_ALARM

        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
        Log.d("AlarmRingingActivity", "Alarm ringing: $alarmId")

        configureShowOverLockScreenAndWakeScreen()
        setContent {
            val is24Hour by timeFormatStore.is24Hour.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalIs24HourFormat provides is24Hour) {
                OndoriTheme {
                    AlarmRingingScreen(
                        onAlarmHandled = {
                            finish()
                        },
                    )
                }
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