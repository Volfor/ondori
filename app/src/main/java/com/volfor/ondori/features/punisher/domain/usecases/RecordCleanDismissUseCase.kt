package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import java.time.Clock
import javax.inject.Inject

class RecordCleanDismissUseCase @Inject constructor(
    private val repo: PunisherRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke() {
        repo.applyReward()
        repo.setLastDismissedAlarmTime(clock.millis())
    }
}