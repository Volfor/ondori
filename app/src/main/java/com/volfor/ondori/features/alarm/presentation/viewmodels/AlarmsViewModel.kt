package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.app.ui.Message
import com.volfor.ondori.app.ui.SnackbarManager
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.ComputeAlarmRemainingTimeUseCase
import com.volfor.ondori.features.alarm.domain.usecases.CreateAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.DeleteAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.DisableAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.EnableAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.ObserveAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.RescheduleEnabledAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.UpdateAlarmUseCase
import com.volfor.ondori.features.alarm.presentation.formatters.AlarmScheduledMessageFormatter
import com.volfor.ondori.features.punisher.domain.usecases.ObserveScoreUseCase
import com.volfor.ondori.features.punisher.domain.usecases.UpdateScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.MarkNotificationPermissionAsRequestedUseCase
import com.volfor.ondori.features.settings.domain.usecases.ObserveNotificationPermissionRequestedUseCase
import com.volfor.ondori.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
    private val snackbarManager: SnackbarManager,
    observeAlarms: ObserveAlarmsUseCase,
    observeScore: ObserveScoreUseCase,
    private val _createAlarm: CreateAlarmUseCase,
    private val _updateAlarm: UpdateAlarmUseCase,
    private val _deleteAlarm: DeleteAlarmUseCase,
    private val _enableAlarm: EnableAlarmUseCase,
    private val _disableAlarm: DisableAlarmUseCase,
    observeNotificationPermissionRequested: ObserveNotificationPermissionRequestedUseCase,
    private val _markNotificationPermissionAsRequested: MarkNotificationPermissionAsRequestedUseCase,
    private val computeAlarmRemainingTime: ComputeAlarmRemainingTimeUseCase,
    private val alarmScheduledMessageFormatter: AlarmScheduledMessageFormatter,
    private val _updateScore: UpdateScoreUseCase,
    private val _rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) : ViewModel() {

    private var rescheduleDebounceJob: Job? = null

    private val _selectedAlarm = MutableStateFlow<Alarm?>(null)

    val snackbarMessages: StateFlow<List<Message>> = snackbarManager.messages

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
        val alarm = Alarm(
            hour = hour,
            minute = minute,
            enabled = true,
        )
        _createAlarm(alarm = alarm)
        showAlarmScheduledMessage(alarm)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        _updateAlarm(alarm)
        if (alarm.enabled) {
            showAlarmScheduledMessage(alarm)
        }
    }

    fun setAlarmEnabled(alarm: Alarm, enabled: Boolean) = viewModelScope.launch {
        if (enabled) {
            _enableAlarm(alarmId = alarm.id)
            showAlarmScheduledMessage(alarm)
        } else {
            _disableAlarm(alarmId = alarm.id)
        }
    }

    private suspend fun showAlarmScheduledMessage(alarm: Alarm) {
        val remainingTime = computeAlarmRemainingTime(alarm)
        val (messageTextId, args) = alarmScheduledMessageFormatter.format(remainingTime)
        snackbarManager.showMessage(messageTextId, args)
    }

    fun onSnackbarShown(messageId: Long) {
        snackbarManager.setMessageShown(messageId)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        _deleteAlarm(alarmId = alarm.id)
    }

    fun markNotificationPermissionAsRequested() = viewModelScope.launch {
        _markNotificationPermissionAsRequested()
    }

    fun updateScore(score: Int) = viewModelScope.launch {
        _updateScore(score)
        rescheduleDebounceJob?.cancel()
        rescheduleDebounceJob = viewModelScope.launch {
            delay(500L.milliseconds)
            _rescheduleEnabledAlarms()
        }
    }
}
