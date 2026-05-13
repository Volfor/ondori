package com.volfor.ondori.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import com.volfor.ondori.features.settings.domain.usecases.ObserveIncreasingVolumeEnabledUseCase
import com.volfor.ondori.features.settings.domain.usecases.ObserveSnoozeMinutesUseCase
import com.volfor.ondori.features.settings.domain.usecases.SetIncreasingVolumeEnabledUseCase
import com.volfor.ondori.features.settings.domain.usecases.SetSnoozeMinutesUseCase
import com.volfor.ondori.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val snoozeMinutes: Int = SettingsRepository.DEFAULT_SNOOZE_MINUTES,
    val increasingVolumeEnabled: Boolean = SettingsRepository.DEFAULT_INCREASING_VOLUME_ENABLED,
    val isLoading: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSnoozeMinutes: ObserveSnoozeMinutesUseCase,
    observeIncreasingVolumeEnabled: ObserveIncreasingVolumeEnabledUseCase,
    private val _setSnoozeMinutes: SetSnoozeMinutesUseCase,
    private val _setIncreasingVolumeEnabled: SetIncreasingVolumeEnabledUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        observeSnoozeMinutes(),
        observeIncreasingVolumeEnabled(),
    ) { snooze, increasingVolume ->
        SettingsUiState(
            snoozeMinutes = snooze,
            increasingVolumeEnabled = increasingVolume,
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = SettingsUiState(isLoading = true),
    )

    fun setSnoozeMinutes(minutes: Int) = viewModelScope.launch {
        _setSnoozeMinutes(minutes)
    }

    fun setIncreasingVolumeEnabled(enabled: Boolean) = viewModelScope.launch {
        _setIncreasingVolumeEnabled(enabled)
    }
}
