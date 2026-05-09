package com.volfor.ondori

import android.media.AudioManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.volfor.ondori.app.OndoriApp
import com.volfor.ondori.app.time.LocalIs24HourFormat
import com.volfor.ondori.app.time.TimeFormatStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var timeFormatStore: TimeFormatStore

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_ALARM
        setContent {
            val is24Hour by timeFormatStore.is24Hour.collectAsStateWithLifecycle()
            CompositionLocalProvider(LocalIs24HourFormat provides is24Hour) {
                OndoriApp()
            }
        }
    }
}