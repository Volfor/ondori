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

class ComputeTimeUntilAlarmUseCaseTest {

    private lateinit var timeCalculator: AlarmTimeCalculator

    private val alarm = Alarm(id = 1L, hour = 7, minute = 30, enabled = true)

    @Before
    fun setup() {
        timeCalculator = mockk()
    }

    private fun useCase() = ComputeTimeUntilAlarmUseCase(timeCalculator)

    @Test
    fun `returns remaining time until computed trigger time`() = runBlocking {
        val triggerTime = 10_000_000L
        val timeUntilAlarm = 3_600_000L

        coEvery {
            timeCalculator.computeNextTriggerTime(
                alarm.hour,
                alarm.minute,
                alarm.repeatDays
            )
        } returns triggerTime
        every { timeCalculator.computeTimeUntilTrigger(triggerTime) } returns timeUntilAlarm

        val result = useCase()(alarm)

        assertEquals(timeUntilAlarm, result)
    }
}
