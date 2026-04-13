package com.volfor.ondori.features.prefs.domain.usecases

import com.volfor.ondori.features.prefs.domain.repositories.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotificationPermissionRequestedUseCase @Inject constructor(
    private val repo: AppPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repo.observeHasRequestedNotificationPermission()
    }
}