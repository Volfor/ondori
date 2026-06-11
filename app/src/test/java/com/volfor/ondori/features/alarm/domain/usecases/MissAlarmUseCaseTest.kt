package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.MissedAlarmNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class MissAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var ringer: AlarmRinger
    private lateinit var missedNotifier: MissedAlarmNotifier

    @Before
    fun setup() {
        repo = mockk()
        ringer = mockk(relaxed = true)
        missedNotifier = mockk(relaxed = true)
    }

    private fun useCase() = MissAlarmUseCase(ringer, repo, missedNotifier)

    @Test
    fun `invalid alarm only stops ringing`() = runBlocking {
        coEvery { repo.getAlarm(-1L) } returns null

        useCase()(-1L)

        verify { ringer.stopRinging(-1L) }
        verify(exactly = 0) { missedNotifier.notifyMissed(any()) }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
    }

    @Test
    fun `one shot missed alarm stops ringing disables and notifies`() = runBlocking {
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
        verify(exactly = 1) { missedNotifier.notifyMissed(alarm) }
    }

    @Test
    fun `repeating missed alarm stops ringing and notifies without disabling`() = runBlocking {
        val alarm = Alarm(
            id = 2L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.TUESDAY),
        )
        coEvery { repo.getAlarm(2L) } returns alarm

        useCase()(2L)

        verify { ringer.stopRinging(2L) }
        verify(exactly = 1) { missedNotifier.notifyMissed(alarm) }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
    }

    @Test
    fun `stops ringing before disabling one shot then notifies`() = runBlocking {
        val alarm = Alarm(
            id = 2L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = emptySet(),
        )
        coEvery { repo.getAlarm(2L) } returns alarm
        coEvery { repo.disableAlarm(2L) } just runs

        useCase()(2L)

        coVerifyOrder {
            ringer.stopRinging(2L)
            repo.disableAlarm(2L)
            missedNotifier.notifyMissed(alarm)
        }
    }
}
