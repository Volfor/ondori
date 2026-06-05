package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.punisher.domain.usecases.ApplyPenaltyUseCase
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ApplyPenaltyAndRescheduleEnabledAlarmsUseCaseTest {

    private lateinit var applyPenalty: ApplyPenaltyUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        applyPenalty = mockk(relaxed = true)
        rescheduleEnabledAlarms = mockk(relaxed = true)
    }

    private fun useCase() =
        ApplyPenaltyAndRescheduleEnabledAlarmsUseCase(applyPenalty, rescheduleEnabledAlarms)

    @Test
    fun `applies penalty then reschedules all enabled alarms`() = runBlocking {
        useCase()()

        coVerifyOrder {
            applyPenalty()
            rescheduleEnabledAlarms()
        }
    }
}
