package com.volfor.ondori.features.settings.domain.usecases

import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSnoozeMinutesUseCase @Inject constructor(
    private val repo: SettingsRepository,
) {
    operator fun invoke(): Flow<Int> {
        return repo.observeSnoozeMinutes()
    }
}
