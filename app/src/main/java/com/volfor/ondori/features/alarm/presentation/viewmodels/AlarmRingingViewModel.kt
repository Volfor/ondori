package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the alarm ringing screen.
 */
data class AlarmRingingUiState(
    val alarm: Alarm? = null,
    val isLoading: Boolean = false,
    val isAlarmHandled: Boolean = false,
)

@HiltViewModel
class AlarmRingingViewModel @Inject constructor(
    private val getAlarm: GetAlarmUseCase,
    private val snoozeAlarm: SnoozeAlarmUseCase,
    private val dismissAlarm: DismissAlarmUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val alarmId: Long? = savedStateHandle["alarm_id"]

    private val _uiState = MutableStateFlow(AlarmRingingUiState())
    val uiState: StateFlow<AlarmRingingUiState> = _uiState.asStateFlow()

    init {
        if (alarmId != null) {
            loadAlarm(alarmId)
        }
    }

    fun snooze() = viewModelScope.launch {
        alarmId ?: return@launch
        snoozeAlarm(alarmId)
        _uiState.update { it.copy(isAlarmHandled = true) }
    }


    fun dismiss() = viewModelScope.launch {
        alarmId ?: return@launch
        dismissAlarm(alarmId)
        _uiState.update { it.copy(isAlarmHandled = true) }
    }

    private fun loadAlarm(alarmId: Long) {
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            getAlarm(alarmId).let { alarm ->
                if (alarm != null) {
                    _uiState.update {
                        it.copy(
                            alarm = alarm,
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}
