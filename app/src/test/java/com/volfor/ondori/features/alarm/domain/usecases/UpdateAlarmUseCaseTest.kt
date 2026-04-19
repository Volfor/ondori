package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class UpdateAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduleAlarm: ScheduleAlarmUseCase
    private lateinit var cancelAlarm: CancelAlarmUseCase

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
        cancelAlarm = mockk()
        coEvery { repo.updateAlarm(any()) } just runs
        coEvery { scheduleAlarm(any()) } just runs
        coEvery { cancelAlarm(any()) } just runs
    }

    @Test
    fun `persists then schedules when alarm is enabled`() = runBlocking {
        val alarm = Alarm(
            id = 5L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.FRIDAY),
        )

        val useCase = UpdateAlarmUseCase(repo, scheduleAlarm, cancelAlarm)
        useCase(alarm)

        coVerifyOrder {
            repo.updateAlarm(alarm)
            scheduleAlarm(alarm)
        }
        coVerify(exactly = 0) { cancelAlarm(any()) }
    }

    @Test
    fun `persists then cancels when alarm is disabled`() = runBlocking {
        val alarm = Alarm(
            id = 8L,
            hour = 7,
            minute = 0,
            enabled = false,
            repeatDays = setOf(DayOfWeek.FRIDAY),
        )

        val useCase = UpdateAlarmUseCase(repo, scheduleAlarm, cancelAlarm)
        useCase(alarm)

        coVerifyOrder {
            repo.updateAlarm(alarm)
            cancelAlarm(8L)
        }
        coVerify(exactly = 0) { scheduleAlarm(any()) }
    }
}
