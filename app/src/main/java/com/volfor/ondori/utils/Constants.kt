package com.volfor.ondori.utils

object Constants {
    const val EXTRA_ALARM_ID = "alarm_id"

    object RequestCodes {
        const val ALARM_NOTIFICATION_FULL_SCREEN = 10_000
        const val ALARM_NOTIFICATION_SNOOZE = 1001
        const val ALARM_NOTIFICATION_STOP = 1002
        const val ALARM_NOTIFICATION_DISMISS = 1003
        const val ALARM_NOTIFICATION_MISSED = 1004
    }

    object Notifications {
        const val FIRING_ALARMS_CHANNEL_ID = "firing_alarms_channel"
        const val FIRING_ALARM_NOTIFICATION_ID = 1

        const val MISSED_ALARMS_CHANNEL_ID = "missed_alarms_channel"
        const val MISSED_ALARM_NOTIFICATION_TAG = "missed_alarm"
    }

    object Alarm {
        const val MISSED_TIMEOUT_MILLIS: Long = 15 * 60 * 1000

        const val GRADUAL_VOLUME_RAMP_MILLIS: Long = 30 * 1000
    }
}