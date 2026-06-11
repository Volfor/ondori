@file:OptIn(ExperimentalCoroutinesApi::class)

package com.volfor.ondori.features.alarm.data.services

import com.volfor.ondori.features.alarm.domain.services.RingingAlarmStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RingingAlarmStoreImpl @Inject constructor() : RingingAlarmStore {
    private val _ringingAlarmId = MutableStateFlow<Long?>(null)
    override val ringingAlarmId: StateFlow<Long?> = _ringingAlarmId.asStateFlow()

    private val _stoppedAlarmId = MutableSharedFlow<Long>(replay = 1)
    override val stoppedAlarmId: SharedFlow<Long> = _stoppedAlarmId.asSharedFlow()

    override fun setRingingAlarm(alarmId: Long) {
        _stoppedAlarmId.resetReplayCache()
        _ringingAlarmId.value = alarmId
    }

    override fun clear() {
        val id = _ringingAlarmId.value ?: return
        _ringingAlarmId.value = null
        _stoppedAlarmId.tryEmit(id)
    }
}