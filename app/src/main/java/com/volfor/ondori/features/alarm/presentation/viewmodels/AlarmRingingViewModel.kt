package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.RingingAlarmStore
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.GetScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.GetSnoozeMinutesUseCase
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the alarm ringing screen.
 */
data class AlarmRingingUiState(
    val alarm: Alarm? = null,
    val score: Int = 0,
    val isLoading: Boolean = false,
    val isAlarmHandled: Boolean = false,
    val snoozeMinutes: Int? = null,
)

@HiltViewModel
class AlarmRingingViewModel @Inject constructor(
    private val getAlarm: GetAlarmUseCase,
    private val getSnoozeMinutes: GetSnoozeMinutesUseCase,
    private val getScore: GetScoreUseCase,
    private val snoozeAlarm: SnoozeAlarmUseCase,
    private val dismissAlarm: DismissAlarmUseCase,
    ringingAlarmStore: RingingAlarmStore,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val alarmId = savedStateHandle.getStateFlow(EXTRA_ALARM_ID, null as Long?)

    var uiState by mutableStateOf(AlarmRingingUiState())
        private set

    init {
        loadInfo()
        viewModelScope.launch {
            alarmId.filterNotNull().collectLatest { loadAlarm(it) }
        }

        ringingAlarmStore.ringingAlarmId.filterNotNull().onEach {
            onNewAlarm(it)
        }.launchIn(viewModelScope)

        ringingAlarmStore.stoppedAlarmId.onEach {
            if (it == alarmId.value) {
                uiState = uiState.copy(isAlarmHandled = true)
            }
        }.launchIn(viewModelScope)
    }

    fun onNewAlarm(newAlarmId: Long) {
        if (alarmId.value == newAlarmId) return
        savedStateHandle[EXTRA_ALARM_ID] = newAlarmId
    }

    fun snooze() = viewModelScope.launch {
        val id = alarmId.value ?: return@launch
        snoozeAlarm(id)
        uiState = uiState.copy(isAlarmHandled = true)
    }

    fun dismiss() = viewModelScope.launch {
        val id = alarmId.value ?: return@launch
        dismissAlarm(id)
        uiState = uiState.copy(isAlarmHandled = true)
    }

    private suspend fun loadAlarm(alarmId: Long) {
        uiState = uiState.copy(isLoading = true)
        val alarm = getAlarm(alarmId)
        uiState = uiState.copy(alarm = alarm, isLoading = false)
    }

    private fun loadInfo() = viewModelScope.launch {
        val score = getScore()
        val snoozeMinutes = getSnoozeMinutes()
        uiState = uiState.copy(score = score, snoozeMinutes = snoozeMinutes)
    }
}
