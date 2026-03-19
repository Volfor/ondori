package com.volfor.ondori.utils

object Constants {
    const val EXTRA_ALARM_ID = "alarm_id"

    object RequestCodes {
        const val ALARM_NOTIFICATION_FULL_SCREEN = 0
        const val ALARM_NOTIFICATION_SNOOZE = 1001
        const val ALARM_NOTIFICATION_STOP = 1002
        const val ALARM_NOTIFICATION_DISMISS = 1003
    }

    object Notifications {
        const val FIRING_ALARMS_CHANNEL_ID = "firing_alarms_channel"
        const val FIRING_ALARM_NOTIFICATION_ID = 1
    }

}