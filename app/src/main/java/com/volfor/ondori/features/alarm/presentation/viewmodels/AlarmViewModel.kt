package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.CreateAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmsStreamUseCase
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
    val items: List<Alarm> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class AlarmViewModel @Inject constructor(
    getAlarmsStream: GetAlarmsStreamUseCase,
    private val createAlarm: CreateAlarmUseCase,
) : ViewModel() {

    val uiState: StateFlow<AlarmsUiState> = getAlarmsStream().map { alarms ->
        AlarmsUiState(
            items = alarms, isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = AlarmsUiState(isLoading = true)
    )

    fun createAlarm(hour: Int, minute: Int) = viewModelScope.launch {
        createAlarm(
            alarm = Alarm(
                hour = hour,
                minute = minute,
                enabled = true,
            )
        )
    }
}
