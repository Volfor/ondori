package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class DismissAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduleAlarm: ScheduleAlarmUseCase
    private lateinit var ringer: AlarmRinger

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
        ringer = mockk()
    }

    @Test
    fun `missing alarm only stops ringing`() = runBlocking {
        coEvery { repo.getAlarm(-1L) } returns null
        every { ringer.stopRinging(any()) } just runs

        val useCase = DismissAlarmUseCase(repo, scheduleAlarm, ringer)
        useCase(-1L)

        verify { ringer.stopRinging(-1L) }

        coVerify(exactly = 0) { scheduleAlarm(any()) }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
    }

    @Test
    fun `one shot alarm disables and stops ringing`() = runBlocking {
        val alarm = Alarm(
            id = 1L,
            hour = 9,
            minute = 0,
            enabled = true,
            repeatDays = emptySet(),
        )
        coEvery { repo.getAlarm(1L) } returns alarm
        coEvery { repo.disableAlarm(1L) } just runs
        every { ringer.stopRinging(any()) } just runs

        val useCase = DismissAlarmUseCase(repo, scheduleAlarm, ringer)
        useCase(1L)

        coVerify { repo.disableAlarm(1L) }
        coVerify(exactly = 0) { scheduleAlarm(any()) }
        verify { ringer.stopRinging(1L) }
    }

    @Test
    fun `repeating alarm schedules next and stops ringing`() = runBlocking {
        val alarm = Alarm(
            id = 2L,
            hour = 8,
            minute = 30,
            enabled = true,
            repeatDays = setOf(DayOfWeek.MONDAY),
        )
        coEvery { repo.getAlarm(2L) } returns alarm
        coEvery { scheduleAlarm(any()) } just runs
        every { ringer.stopRinging(any()) } just runs

        val useCase = DismissAlarmUseCase(repo, scheduleAlarm, ringer)
        useCase(2L)

        coVerify { scheduleAlarm(alarm) }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
        verify { ringer.stopRinging(2L) }
    }
}
