package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.ApplyPenaltyUseCase
import com.volfor.ondori.features.settings.domain.usecases.GetSnoozeMinutesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SnoozeAlarmUseCaseTest {

    private lateinit var timeCalculator: AlarmTimeCalculator
    private lateinit var scheduler: AlarmScheduler
    private lateinit var ringer: AlarmRinger
    private lateinit var applyPenalty: ApplyPenaltyUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase
    private lateinit var getSnoozeMinutes: GetSnoozeMinutesUseCase

    @Before
    fun setup() {
        timeCalculator = mockk()
        scheduler = mockk(relaxed = true)
        ringer = mockk(relaxed = true)
        applyPenalty = mockk(relaxed = true)
        rescheduleEnabledAlarms = mockk(relaxed = true)
        getSnoozeMinutes = mockk()
        coEvery { getSnoozeMinutes() } returns 9
    }

    private fun useCase() = SnoozeAlarmUseCase(
        timeCalculator,
        scheduler,
        ringer,
        applyPenalty,
        rescheduleEnabledAlarms,
        getSnoozeMinutes,
    )

    @Test
    fun `stops ringing, applies penalty, reschedules, and schedules snooze`() = runBlocking {
        val id = 3L
        val trigger = 20_000L

        every { timeCalculator.computeSnoozeTriggerTime(9) } returns trigger

        useCase()(id)

        verify(exactly = 1) { ringer.stopRinging(id) }
        coVerify(exactly = 1) { applyPenalty() }
        coVerify(exactly = 1) { rescheduleEnabledAlarms() }
        verify(exactly = 1) { scheduler.scheduleAlarm(id, trigger) }
    }

    @Test
    fun `reschedules enabled alarms before scheduling the snooze trigger`() = runBlocking {
        val id = 4L
        val trigger = 30_000L

        every { timeCalculator.computeSnoozeTriggerTime(9) } returns trigger

        useCase()(id)

        coVerifyOrder {
            rescheduleEnabledAlarms()
            scheduler.scheduleAlarm(id, trigger)
        }
    }
}
