package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.CancelAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.ScheduleAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val scheduleAlarm: ScheduleAlarmUseCase,
    private val cancelAlarm: CancelAlarmUseCase,
    private val getAlarms: GetAlarmsUseCase,
) : ViewModel() {

    private val _alarms = getAlarms()

    val uiState = AlarmsUiState(
        items = _alarms, isLoading = false
    )

    fun setAlarmInOneMinute() {
        val time = System.currentTimeMillis() + 10_000
        scheduleAlarm(0, time)
    }

    fun cancelAlarm() {
        cancelAlarm(0)
    }
}