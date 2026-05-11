package com.volfor.ondori.features.settings.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import com.volfor.ondori.features.settings.domain.usecases.ObserveSnoozeMinutesUseCase
import com.volfor.ondori.features.settings.domain.usecases.SetSnoozeMinutesUseCase
import com.volfor.ondori.utils.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val snoozeMinutes: Int = SettingsRepository.DEFAULT_SNOOZE_MINUTES,
    val isLoading: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSnoozeMinutes: ObserveSnoozeMinutesUseCase,
    private val _setSnoozeMinutes: SetSnoozeMinutesUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = observeSnoozeMinutes()
        .map { SettingsUiState(snoozeMinutes = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = SettingsUiState(isLoading = true),
        )

    fun setSnoozeMinutes(minutes: Int) = viewModelScope.launch {
        _setSnoozeMinutes(minutes)
    }
}
