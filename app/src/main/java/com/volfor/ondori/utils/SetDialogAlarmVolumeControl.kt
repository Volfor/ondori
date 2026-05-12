package com.volfor.ondori.utils

import android.media.AudioManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun SetDialogAlarmVolumeControl() {
    val view = LocalView.current
    DisposableEffect(view) {
        (view.parent as? DialogWindowProvider)?.window?.let {
            it.volumeControlStream = AudioManager.STREAM_ALARM
        }
        onDispose { }
    }
}