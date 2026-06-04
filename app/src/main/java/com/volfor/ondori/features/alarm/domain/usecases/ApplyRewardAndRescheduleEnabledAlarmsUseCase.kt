package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.punisher.domain.usecases.ApplyRewardUseCase
import javax.inject.Inject

class ApplyRewardAndRescheduleEnabledAlarmsUseCase @Inject constructor(
    private val applyReward: ApplyRewardUseCase,
    private val rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase,
) {
    suspend operator fun invoke() {
        applyReward()
        rescheduleEnabledAlarms()
    }
}