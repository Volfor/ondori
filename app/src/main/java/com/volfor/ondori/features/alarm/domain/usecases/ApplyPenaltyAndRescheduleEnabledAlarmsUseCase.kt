package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.punisher.domain.usecases.ApplyPenaltyUseCase
import javax.inject.Inject

class ApplyPenaltyAndRescheduleEnabledAlarmsUseCase @Inject constructor(
    private val applyPenalty: ApplyPenaltyUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke() {
        applyPenalty()
        rescheduleEnabledAlarms()
    }
}