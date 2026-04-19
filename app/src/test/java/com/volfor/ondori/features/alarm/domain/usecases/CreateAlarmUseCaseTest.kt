package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
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

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
    }

    @Test
    fun `creates alarm in repository before scheduling`() = runBlocking {
        val alarm = Alarm(
            id = 0L,
            hour = 6,
            minute = 30,
            enabled = true,
        )
        coEvery { repo.createAlarm(alarm) } returns 1L
        coEvery { scheduleAlarm(any()) } just runs

        val useCase = CreateAlarmUseCase(repo, scheduleAlarm)
        useCase(alarm)

        coVerifyOrder {
            repo.createAlarm(alarm)
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

        val useCase = CreateAlarmUseCase(repo, scheduleAlarm)
        useCase(alarm)

        assertEquals(alarm.copy(id = assignedId), scheduled.captured)
        coVerify(exactly = 1) { scheduleAlarm(any()) }
    }
}
