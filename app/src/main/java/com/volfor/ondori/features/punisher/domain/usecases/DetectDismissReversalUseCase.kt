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
    suspend operator fun invoke(newAlarmTriggerTime: Long) {
        val lastDismissedAt = repo.getLastDismissedAlarmTime() ?: return
        val now = clock.millis()
        if (!policy.isDismissReversal(lastDismissedAt, now, newAlarmTriggerTime)) return

        repo.applyDismissReversalPenalty()
        repo.setLastDismissedAlarmTime(null)
    }
}
