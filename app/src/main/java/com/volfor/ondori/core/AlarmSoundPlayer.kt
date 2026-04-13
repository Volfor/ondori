package com.volfor.ondori.core

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.core.net.toUri
import com.volfor.ondori.R
import com.volfor.ondori.features.alarm.domain.entities.AlarmSound
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var mediaPlayer: MediaPlayer? = null

    private val audioAttributes by lazy {
        AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
    }

    fun play(sound: AlarmSound) {
        stop()
        when (sound) {
            AlarmSound.Silent -> Unit
            AlarmSound.Default -> playRaw(R.raw.rooster)
            is AlarmSound.Custom -> playUri(sound.uri)
        }
    }

    private fun playRaw(resourceId: Int) {
        val player = MediaPlayer.create(
            context.applicationContext,
            resourceId,
            audioAttributes,
            AudioManager.AUDIO_SESSION_ID_GENERATE,
        ) ?: return

        player.apply {
            isLooping = true
            start()
        }
        mediaPlayer = player
    }

    private fun playUri(uriString: String) {
        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                setDataSource(context.applicationContext, uriString.toUri())
                isLooping = true
                prepare()
                start()
            }
        } catch (_: Exception) {
            stop()
            playRaw(R.raw.rooster)
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}