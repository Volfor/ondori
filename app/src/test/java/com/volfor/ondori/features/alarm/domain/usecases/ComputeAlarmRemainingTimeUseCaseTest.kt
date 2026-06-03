package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ComputeAlarmRemainingTimeUseCaseTest {

    private lateinit var timeCalculator: AlarmTimeCalculator
    private lateinit var computeAlarmTriggerTime: ComputeAlarmTriggerTimeUseCase

    private val alarm = Alarm(id = 1L, hour = 7, minute = 30, enabled = true)

    @Before
    fun setup() {
        timeCalculator = mockk()
        computeAlarmTriggerTime = mockk()
    }

    private fun useCase() = ComputeAlarmRemainingTimeUseCase(timeCalculator, computeAlarmTriggerTime)

    @Test
    fun `returns remaining time until computed trigger time`() = runBlocking {
        val triggerTime = 10_000_000L
        val remainingTime = 3_600_000L

        coEvery { computeAlarmTriggerTime(alarm) } returns triggerTime
        every { timeCalculator.computeRemainingTime(triggerTime) } returns remainingTime

        val result = useCase()(alarm)

        assertEquals(remainingTime, result)
    }
}
