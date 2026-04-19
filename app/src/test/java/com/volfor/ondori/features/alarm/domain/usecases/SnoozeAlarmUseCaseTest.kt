package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
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

    @Before
    fun setup() {
        timeCalculator = mockk()
        scheduler = mockk(relaxed = true)
        ringer = mockk(relaxed = true)
    }

    @Test
    fun `stops ringing and re-schedules alarm`() = runBlocking {
        val id = 3L
        val trigger = 20_000L

        every { timeCalculator.computeSnoozeTriggerTime() } returns trigger

        val useCase = SnoozeAlarmUseCase(timeCalculator, scheduler, ringer)
        useCase(id)

        verify(exactly = 1) { ringer.stopRinging(id) }
        verify(exactly = 1) { timeCalculator.computeSnoozeTriggerTime() }
        verify(exactly = 1) { scheduler.scheduleAlarm(id, trigger) }
    }


}
