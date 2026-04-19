package com.volfor.ondori.features.settings.domain.usecases

import com.volfor.ondori.features.settings.domain.repositories.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationPermissionRequestedUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repo.observeHasRequestedNotificationPermission()
    }
}