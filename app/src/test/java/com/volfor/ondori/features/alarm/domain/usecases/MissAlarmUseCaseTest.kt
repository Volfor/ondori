package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.MissedAlarmNotifier
import com.volfor.ondori.features.punisher.domain.usecases.ApplyPenaltyUseCase
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
    private lateinit var applyPenalty: ApplyPenaltyUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        repo = mockk()
        ringer = mockk(relaxed = true)
        missedNotifier = mockk(relaxed = true)
        applyPenalty = mockk(relaxed = true)
        rescheduleEnabledAlarms = mockk(relaxed = true)
    }

    @Test
    fun `invalid alarm only stops ringing`() = runBlocking {
        coEvery { repo.getAlarm(-1L) } returns null

        val useCase =
            MissAlarmUseCase(ringer, repo, missedNotifier, applyPenalty, rescheduleEnabledAlarms)
        useCase(-1L)

        verify { ringer.stopRinging(-1L) }
        verify(exactly = 0) { missedNotifier.notifyMissed(any()) }
        coVerify(exactly = 0) { applyPenalty() }
        coVerify(exactly = 0) { rescheduleEnabledAlarms() }
    }

    @Test
    fun `missed alarm stops, notifies, penalizes and reschedules without disabling`() =
        runBlocking {
            val alarm = Alarm(
                id = 1L,
                hour = 9,
                minute = 0,
                enabled = true,
                repeatDays = emptySet(),
            )
            coEvery { repo.getAlarm(1L) } returns alarm

            val useCase = MissAlarmUseCase(
                ringer,
                repo,
                missedNotifier,
                applyPenalty,
                rescheduleEnabledAlarms
            )
            useCase(1L)

            verify { ringer.stopRinging(1L) }
            verify(exactly = 1) { missedNotifier.notifyMissed(alarm) }
            coVerify(exactly = 1) { applyPenalty() }
            coVerify(exactly = 1) { rescheduleEnabledAlarms() }
            coVerify(exactly = 0) { repo.disableAlarm(any()) }
        }

    @Test
    fun `stops ringing before notifying missed, apply penalty before rescheduling`() = runBlocking {
        val alarm = Alarm(
            id = 2L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.TUESDAY),
        )
        coEvery { repo.getAlarm(2L) } returns alarm

        val useCase =
            MissAlarmUseCase(ringer, repo, missedNotifier, applyPenalty, rescheduleEnabledAlarms)
        useCase(2L)

        coVerifyOrder {
            ringer.stopRinging(2L)
            missedNotifier.notifyMissed(alarm)
            applyPenalty()
            rescheduleEnabledAlarms()
        }
    }
}
