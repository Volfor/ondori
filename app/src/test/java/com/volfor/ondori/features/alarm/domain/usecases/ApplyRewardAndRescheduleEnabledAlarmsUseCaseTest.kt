package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.punisher.domain.usecases.ApplyRewardUseCase
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ApplyRewardAndRescheduleEnabledAlarmsUseCaseTest {

    private lateinit var applyReward: ApplyRewardUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        applyReward = mockk(relaxed = true)
        rescheduleEnabledAlarms = mockk(relaxed = true)
    }

    private fun useCase() =
        ApplyRewardAndRescheduleEnabledAlarmsUseCase(applyReward, rescheduleEnabledAlarms)

    @Test
    fun `applies reward then reschedules all enabled alarms`() = runBlocking {
        useCase()()

        coVerifyOrder {
            applyReward()
            rescheduleEnabledAlarms()
        }
    }
}
