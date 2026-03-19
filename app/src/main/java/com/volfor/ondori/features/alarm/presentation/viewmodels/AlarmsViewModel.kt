package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.CreateAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.DeleteAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.DisableAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.EnableAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmsStreamUseCase
import com.volfor.ondori.features.alarm.presentation.models.AlarmUiModel
import com.volfor.ondori.features.alarm.presentation.models.toUiModel
import com.volfor.ondori.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UiState for the alarm list screen.
 */
data class AlarmsUiState(
    val items: List<AlarmUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    getAlarmsStream: GetAlarmsStreamUseCase,
    private val _createAlarm: CreateAlarmUseCase,
    private val _deleteAlarm: DeleteAlarmUseCase,
    private val _enableAlarm: EnableAlarmUseCase,
    private val _disableAlarm: DisableAlarmUseCase,
) : ViewModel() {

    val uiState: StateFlow<AlarmsUiState> = getAlarmsStream().map { alarms ->
        AlarmsUiState(
            items = alarms.map { it.toUiModel() }, isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = AlarmsUiState(isLoading = true)
    )

    fun createAlarm(hour: Int, minute: Int) = viewModelScope.launch {
        _createAlarm(
            alarm = Alarm(
                hour = hour,
                minute = minute,
                enabled = true,
            )
        )
    }

    fun setAlarmEnabled(alarm: AlarmUiModel, enabled: Boolean) = viewModelScope.launch {
        if (enabled) {
            _enableAlarm(alarmId = alarm.id)
        } else {
            _disableAlarm(alarmId = alarm.id)
        }
    }

    fun deleteAlarm(alarm: AlarmUiModel) = viewModelScope.launch {
        _deleteAlarm(alarmId = alarm.id)
    }
}
