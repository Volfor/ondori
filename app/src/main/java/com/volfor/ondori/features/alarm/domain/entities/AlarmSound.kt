package com.volfor.ondori.features.alarm.domain.entities

sealed interface AlarmSound {
    data object Default: AlarmSound
    data object Silent: AlarmSound
    /** Content [android.net.Uri] string for a system/user alarm tone. */
    data class Custom(val uri: String): AlarmSound
}