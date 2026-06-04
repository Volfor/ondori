package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.MissedAlarmNotifier
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class MissAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var ringer: AlarmRinger
    private lateinit var missedNotifier: MissedAlarmNotifier
    private lateinit var applyPenaltyAndRescheduleEnabledAlarms: ApplyPenaltyAndRescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        repo = mockk()
        ringer = mockk(relaxed = true)
        missedNotifier = mockk(relaxed = true)
        applyPenaltyAndRescheduleEnabledAlarms = mockk(relaxed = true)
    }

    private fun useCase() =
        MissAlarmUseCase(ringer, repo, missedNotifier, applyPenaltyAndRescheduleEnabledAlarms)

    @Test
    fun `invalid alarm only stops ringing`() = runBlocking {
        coEvery { repo.getAlarm(-1L) } returns null

        useCase()(-1L)

        verify { ringer.stopRinging(-1L) }
        verify(exactly = 0) { missedNotifier.notifyMissed(any()) }
        coVerify(exactly = 0) { applyPenaltyAndRescheduleEnabledAlarms() }
    }

    @Test
    fun `missed alarm stops, notifies, penalizes and reschedules enabled alarms`() = runBlocking {
        val alarm = Alarm(
            id = 1L,
            hour = 9,
            minute = 0,
            enabled = true,
            repeatDays = emptySet(),
        )
        coEvery { repo.getAlarm(1L) } returns alarm

        useCase()(1L)

        verify { ringer.stopRinging(1L) }
        verify(exactly = 1) { missedNotifier.notifyMissed(alarm) }
        coVerify(exactly = 1) { applyPenaltyAndRescheduleEnabledAlarms() }
        coVerify(exactly = 0) { repo.disableAlarm(any()) }
    }

    @Test
    fun `stops ringing before notifying missed then applies penalty and reschedules`() = runBlocking {
        val alarm = Alarm(
            id = 2L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.TUESDAY),
        )
        coEvery { repo.getAlarm(2L) } returns alarm

        useCase()(2L)

        coVerifyOrder {
            ringer.stopRinging(2L)
            missedNotifier.notifyMissed(alarm)
            applyPenaltyAndRescheduleEnabledAlarms()
        }
    }
}
