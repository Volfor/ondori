package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.PunisherPolicy
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import javax.inject.Inject

class GetTimeWithPenaltyOffsetUseCase @Inject constructor(
    private val repo: PunisherRepository,
    private val punisher: PunisherPolicy,
) {
    suspend operator fun invoke(time: Long): Long {
        val penaltyOffset = punisher.penaltyOffsetMillis(repo.getScore())
        return punisher.withPenaltyOffset(time, penaltyOffset)
    }
}