package com.volfor.ondori.features.prefs.domain.usecases

import com.volfor.ondori.features.prefs.domain.repositories.AppPreferencesRepository
import javax.inject.Inject

class SetNotifPermissionPromptShownUseCase @Inject constructor(
    private val repo: AppPreferencesRepository
) {
    suspend operator fun invoke(shown: Boolean) {
        return repo.setNotifPermissionPromptShown(shown)
    }
}