package com.volfor.ondori.core

import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.media.AudioAttributes
import android.os.Build
import android.os.VibrationAttributes
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
        val effect = VibrationEffect.createWaveform(pattern, amplitudes, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val attributes =
                VibrationAttributes.Builder().setUsage(VibrationAttributes.USAGE_ALARM).build()
            vibrator.vibrate(effect, attributes)
        } else {
            val attributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
            @Suppress("DEPRECATION") vibrator.vibrate(effect, attributes)
        }
    }

    fun stop() {
        vibrator.cancel()
    }
}