package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import io.mockk.coEvery
import io.mockk.coVerify
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
    private lateinit var ringer: AlarmRinger
    private lateinit var applyRewardAndRescheduleEnabledAlarms: ApplyRewardAndRescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        repo = mockk()
        ringer = mockk(relaxed = true)
        applyRewardAndRescheduleEnabledAlarms = mockk(relaxed = true)
    }

    private fun useCase() =
        DismissAlarmUseCase(repo, ringer, applyRewardAndRescheduleEnabledAlarms)

    @Test
    fun `missing alarm only stops ringing`() = runBlocking {
        coEvery { repo.getAlarm(-1L) } returns null

        useCase()(-1L)

        verify { ringer.stopRinging(-1L) }
        coVerify(exactly = 0) { applyRewardAndRescheduleEnabledAlarms() }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
    }

    @Test
    fun `one shot alarm disables then applies reward and reschedules enabled alarms`() = runBlocking {
        val alarm = Alarm(
            id = 1L,
            hour = 9,
            minute = 0,
            enabled = true,
            repeatDays = emptySet(),
        )
        coEvery { repo.getAlarm(1L) } returns alarm
        coEvery { repo.disableAlarm(1L) } just runs

        useCase()(1L)

        verify { ringer.stopRinging(1L) }
        coVerify(exactly = 1) { repo.disableAlarm(1L) }
        coVerify(exactly = 1) { applyRewardAndRescheduleEnabledAlarms() }
    }

    @Test
    fun `repeating alarm applies reward and reschedules without disabling`() = runBlocking {
        val alarm = Alarm(
            id = 2L,
            hour = 8,
            minute = 30,
            enabled = true,
            repeatDays = setOf(DayOfWeek.MONDAY),
        )
        coEvery { repo.getAlarm(2L) } returns alarm

        useCase()(2L)

        verify { ringer.stopRinging(2L) }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
        coVerify(exactly = 1) { applyRewardAndRescheduleEnabledAlarms() }
    }
}
