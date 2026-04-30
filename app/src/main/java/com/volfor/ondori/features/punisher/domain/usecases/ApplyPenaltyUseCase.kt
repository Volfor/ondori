package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import javax.inject.Inject

class ApplyPenaltyUseCase @Inject constructor(
    private val repo: PunisherRepository,
) {
    suspend operator fun invoke() {
        repo.applyPenalty()
        // Penalty invalidates any open clean-dismiss window so a later
        // create/update isn't double-penalized as a recreation/rescheduling.
        repo.setLastDismissedAlarmTime(null)
    }
}