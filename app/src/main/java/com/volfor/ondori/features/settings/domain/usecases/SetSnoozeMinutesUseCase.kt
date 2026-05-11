package com.volfor.ondori.features.settings.domain.usecases

import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import javax.inject.Inject

class SetSnoozeMinutesUseCase @Inject constructor(
    private val repo: SettingsRepository,
) {
    suspend operator fun invoke(minutes: Int) {
        repo.setSnoozeMinutes(minutes)
    }
}
