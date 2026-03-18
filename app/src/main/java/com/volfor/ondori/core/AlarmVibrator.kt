package com.volfor.ondori.core

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmVibrator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.applicationContext.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION") context.applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun vibrate() {
        val pattern = longArrayOf(0, 500, 500)
        val amplitudes = intArrayOf(0, 255, 0)

        vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, 0))
    }

    fun stop() {
        vibrator.cancel()
    }
}