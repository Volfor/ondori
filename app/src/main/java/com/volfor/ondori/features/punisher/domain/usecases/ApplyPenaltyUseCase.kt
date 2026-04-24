package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import javax.inject.Inject

class ApplyPenaltyUseCase @Inject constructor(
    private val repo: PunisherRepository,
) {
    suspend operator fun invoke() {
        repo.applyPenalty()
    }
}