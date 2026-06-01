package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.PunisherPolicy
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import java.time.Clock
import javax.inject.Inject

class DetectDismissReversalUseCase @Inject constructor(
    private val repo: PunisherRepository,
    private val policy: PunisherPolicy,
    private val clock: Clock,
) {
    suspend operator fun invoke(newAlarmTriggerTime: Long): Boolean {
        val lastDismissedAt = repo.getLastDismissedAlarmTime() ?: return false
        val now = clock.millis()
        if (!policy.isDismissReversal(lastDismissedAt, now, newAlarmTriggerTime)) return false

        repo.applyDismissReversalPenalty()
        repo.setLastDismissedAlarmTime(null)
        return true
    }
}
