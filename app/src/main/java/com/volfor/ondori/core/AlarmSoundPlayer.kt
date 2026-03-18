package com.volfor.ondori.core

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
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

    fun play(resourceId: Int) {
        stop()

        mediaPlayer = MediaPlayer.create(
            context.applicationContext,
            resourceId,
            audioAttributes,
            AudioManager.AUDIO_SESSION_ID_GENERATE,
        ).apply {
            isLooping = true
            start()
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}