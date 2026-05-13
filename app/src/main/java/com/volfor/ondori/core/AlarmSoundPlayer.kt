package com.volfor.ondori.core

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.core.net.toUri
import com.volfor.ondori.R
import com.volfor.ondori.di.ApplicationScope
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound
import com.volfor.ondori.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

class AlarmSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {
    private companion object {
        private const val ATTRIBUTION_TAG_ALARM = "alarm_playback"
        private const val INITIAL_INCREASING_VOLUME = 0.0f
        private const val VOLUME_RAMP_STEPS = 60
    }

    private var mediaPlayer: MediaPlayer? = null
    private var volumeRampJob: Job? = null

    private fun attributedContext(): Context = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        context.applicationContext.createAttributionContext(ATTRIBUTION_TAG_ALARM)
    } else {
        context.applicationContext
    }

    private val audioAttributes by lazy {
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
    }

    fun play(sound: AlarmSound, gradualVolumeIncrease: Boolean = false) {
        stop()
        when (sound) {
            AlarmSound.Silent -> Unit
            AlarmSound.Default -> playRaw(R.raw.rooster, gradualVolumeIncrease)
            is AlarmSound.Custom -> playUri(sound.uri, gradualVolumeIncrease)
        }
    }

    private fun playRaw(resourceId: Int, gradualVolumeIncrease: Boolean) {
        val player = MediaPlayer.create(
            attributedContext(),
            resourceId,
            audioAttributes,
            AudioManager.AUDIO_SESSION_ID_GENERATE,
        ) ?: return

        player.apply {
            isLooping = true
            if (gradualVolumeIncrease) {
                startVolumeRamp(this)
            }
            start()
        }
        mediaPlayer = player
    }

    private fun playUri(uriString: String, gradualVolumeIncrease: Boolean) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                setDataSource(attributedContext(), uriString.toUri())
                isLooping = true
                prepare()
                if (gradualVolumeIncrease) {
                    startVolumeRamp(this)
                }
                start()
            }
        } catch (_: Exception) {
            stop()
            playRaw(R.raw.rooster, gradualVolumeIncrease)
        }
    }

    private fun startVolumeRamp(player: MediaPlayer) {
        cancelVolumeRamp()

        val duration = Constants.Alarm.GRADUAL_VOLUME_RAMP_MILLIS
        val steps = VOLUME_RAMP_STEPS
        val stepMs = max(100L, duration / steps)

        player.setVolume(INITIAL_INCREASING_VOLUME, INITIAL_INCREASING_VOLUME)
        volumeRampJob = applicationScope.launch(Dispatchers.Main) {
            for (step in 0..steps) {
                val current = mediaPlayer
                if (current == null || current !== player) return@launch
                val volume = step / steps.toFloat()
                try {
                    current.setVolume(volume, volume)
                } catch (_: IllegalStateException) {
                    return@launch
                }
                if (step < steps) delay(stepMs)
            }
        }
    }

    private fun cancelVolumeRamp() {
        volumeRampJob?.cancel()
        volumeRampJob = null
    }

    fun stop() {
        cancelVolumeRamp()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}