package com.volfor.ondori.features.prefs.domain.usecases

import com.volfor.ondori.features.prefs.domain.repositories.AppPreferencesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class IsNotifPermissionPromptShownUseCase @Inject constructor(
    private val repo: AppPreferencesRepository
) {
    suspend operator fun invoke(): Boolean {
        return repo.notifPermissionPromptShownFlow.first()
    }
}