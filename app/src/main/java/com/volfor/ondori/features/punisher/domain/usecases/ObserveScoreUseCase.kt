package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveScoreUseCase @Inject constructor(
    private val repo: PunisherRepository,
) {
    operator fun invoke(): Flow<Int> {
        return repo.observeScore()
    }
}