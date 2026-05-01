package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.GetScoreUseCase
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import com.volfor.ondori.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
)

@HiltViewModel
class AlarmRingingViewModel @Inject constructor(
    private val getAlarm: GetAlarmUseCase,
    private val getScore: GetScoreUseCase,
    private val snoozeAlarm: SnoozeAlarmUseCase,
    private val dismissAlarm: DismissAlarmUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val alarmId: Long? = savedStateHandle[EXTRA_ALARM_ID]

    private val alarm = MutableStateFlow<Alarm?>(null)
    private val score = MutableStateFlow(0)
    private val isLoading = MutableStateFlow(false)
    private val isAlarmHandled = MutableStateFlow(false)

    val uiState: StateFlow<AlarmRingingUiState> = combine(
        alarm, score, isLoading, isAlarmHandled,
    ) { alarm, score, isLoading, isHandled ->
        AlarmRingingUiState(
            alarm = alarm,
            score = score,
            isLoading = isLoading,
            isAlarmHandled = isHandled,
        )
    }.stateIn(
        scope = viewModelScope, started = WhileUiSubscribed, initialValue = AlarmRingingUiState()
    )

    init {
        loadScore()
        if (alarmId != null) {
            loadAlarm(alarmId)
        }
    }

    fun snooze() = viewModelScope.launch {
        alarmId ?: return@launch
        snoozeAlarm(alarmId)
        isAlarmHandled.update { true }
    }

    fun dismiss() = viewModelScope.launch {
        alarmId ?: return@launch
        dismissAlarm(alarmId)
        isAlarmHandled.update { true }
    }

    private fun loadAlarm(alarmId: Long) {
        isLoading.update { true }
        viewModelScope.launch {
            alarm.value = getAlarm(alarmId)
            isLoading.update { false }
        }
    }

    private fun loadScore() = viewModelScope.launch {
        score.value = getScore()
    }
}
