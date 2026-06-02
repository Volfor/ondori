package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.CreateAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.DeleteAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.DisableAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.EnableAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.ObserveAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.UpdateAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.ObserveScoreUseCase
import com.volfor.ondori.features.punisher.domain.usecases.UpdateScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.MarkNotificationPermissionAsRequestedUseCase
import com.volfor.ondori.features.settings.domain.usecases.ObserveNotificationPermissionRequestedUseCase
import com.volfor.ondori.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the alarm list screen.
 */
data class AlarmsUiState(
    val items: List<Alarm> = emptyList(),
    val score: Int = 0,
    val isLoading: Boolean = false,
    val selectedAlarm: Alarm? = null,
    val hasRequestedNotificationPermission: Boolean = false,
)

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    observeAlarms: ObserveAlarmsUseCase,
    observeScore: ObserveScoreUseCase,
    private val _createAlarm: CreateAlarmUseCase,
    private val _updateAlarm: UpdateAlarmUseCase,
    private val _deleteAlarm: DeleteAlarmUseCase,
    private val _enableAlarm: EnableAlarmUseCase,
    private val _disableAlarm: DisableAlarmUseCase,
    observeNotificationPermissionRequested: ObserveNotificationPermissionRequestedUseCase,
    private val _markNotificationPermissionAsRequested: MarkNotificationPermissionAsRequestedUseCase,
    private val _updateScore: UpdateScoreUseCase,
) : ViewModel() {

    private val _selectedAlarm = MutableStateFlow<Alarm?>(null)

    val uiState: StateFlow<AlarmsUiState> = combine(
        observeAlarms(), observeScore(), _selectedAlarm, observeNotificationPermissionRequested(),
    ) { alarms, score, selectedAlarm, hasRequestedNotificationPermission ->
        AlarmsUiState(
            items = alarms.sortedWith(compareBy({ it.hour }, { it.minute }, { it.id })),
            score = score,
            isLoading = false,
            selectedAlarm = selectedAlarm,
            hasRequestedNotificationPermission = hasRequestedNotificationPermission,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = AlarmsUiState(isLoading = true)
    )

    fun selectAlarm(alarm: Alarm) {
        _selectedAlarm.value = alarm
    }

    fun clearSelection() {
        _selectedAlarm.value = null
    }

    fun createAlarm(hour: Int, minute: Int) = viewModelScope.launch {
        _createAlarm(
            alarm = Alarm(
                hour = hour,
                minute = minute,
                enabled = true,
            )
        )
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        _updateAlarm(alarm)
    }

    fun setAlarmEnabled(alarm: Alarm, enabled: Boolean) = viewModelScope.launch {
        if (enabled) {
            _enableAlarm(alarmId = alarm.id)
        } else {
            _disableAlarm(alarmId = alarm.id)
        }
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        _deleteAlarm(alarmId = alarm.id)
    }

    fun markNotificationPermissionAsRequested() = viewModelScope.launch {
        _markNotificationPermissionAsRequested()
    }

    fun updateScore(score: Int) = viewModelScope.launch {
        _updateScore(score)
    }
}
