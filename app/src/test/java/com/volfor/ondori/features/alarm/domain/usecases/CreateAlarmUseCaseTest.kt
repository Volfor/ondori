package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.DetectRecreatedAlarmUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduleAlarm: ScheduleAlarmUseCase
    private lateinit var timeCalculator: AlarmTimeCalculator
    private lateinit var detectRecreatedAlarm: DetectRecreatedAlarmUseCase

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
        timeCalculator = mockk()
        detectRecreatedAlarm = mockk(relaxed = true)
        every { timeCalculator.computeNextTriggerTime(any(), any(), any()) } returns 0L
    }

    private fun useCase() =
        CreateAlarmUseCase(repo, scheduleAlarm, timeCalculator, detectRecreatedAlarm)

    @Test
    fun `creates alarm in repository, detects recreation, then schedules`() = runBlocking {
        val alarm = Alarm(
            id = 0L,
            hour = 6,
            minute = 30,
            enabled = true,
        )
        val triggerTime = 1_700_000_000_000L

        coEvery { repo.createAlarm(alarm) } returns 1L
        every {
            timeCalculator.computeNextTriggerTime(any(), any(), any())
        } returns triggerTime
        coEvery { scheduleAlarm(any()) } just runs

        useCase()(alarm)

        coVerifyOrder {
            repo.createAlarm(alarm)
            detectRecreatedAlarm(triggerTime)
            scheduleAlarm(any())
        }
    }

    @Test
    fun `schedules with id returned from repository not placeholder zero`() = runBlocking {
        val alarm = Alarm(
            id = 0L,
            hour = 6,
            minute = 30,
            enabled = true,
        )
        val assignedId = 69L
        coEvery { repo.createAlarm(alarm) } returns assignedId
        val scheduled = slot<Alarm>()
        coEvery { scheduleAlarm(capture(scheduled)) } just runs

        useCase()(alarm)

        assertEquals(alarm.copy(id = assignedId), scheduled.captured)
        coVerify(exactly = 1) { scheduleAlarm(any()) }
    }
}
