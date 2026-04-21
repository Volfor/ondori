package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class RescheduleEnabledAlarmsUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduleAlarm: ScheduleAlarmUseCase

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
    }

    @Test
    fun `schedules nothing when no enabled alarms`() = runBlocking {
        coEvery { repo.getEnabledAlarms() } returns emptyList()
        coEvery { scheduleAlarm(any()) } just runs

        val useCase = RescheduleEnabledAlarmsUseCase(repo, scheduleAlarm)
        useCase()

        coVerify(exactly = 1) { repo.getEnabledAlarms() }
        coVerify(exactly = 0) { scheduleAlarm(any()) }
    }

    @Test
    fun `schedules each enabled alarm`() = runBlocking {
        val a = Alarm(
            id = 1L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.MONDAY),
            label = "a",
        )
        val b = Alarm(
            id = 2L,
            hour = 8,
            minute = 30,
            enabled = true,
            repeatDays = emptySet(),
            label = "b",
        )
        coEvery { repo.getEnabledAlarms() } returns listOf(a, b)
        coEvery { scheduleAlarm(any()) } just runs

        val useCase = RescheduleEnabledAlarmsUseCase(repo, scheduleAlarm)
        useCase()

        coVerify(exactly = 1) { repo.getEnabledAlarms() }
        coVerify(exactly = 1) { scheduleAlarm(a) }
        coVerify(exactly = 1) { scheduleAlarm(b) }
    }
}
