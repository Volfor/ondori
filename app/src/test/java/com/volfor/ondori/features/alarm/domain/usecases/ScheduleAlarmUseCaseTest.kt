package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ScheduleAlarmUseCaseTest {

    private lateinit var scheduler: AlarmScheduler
    private lateinit var computeScheduledTriggerTime: ComputeAlarmTriggerTimeUseCase

    private val alarm = Alarm(id = 1L, hour = 7, minute = 30, enabled = true)

    @Before
    fun setup() {
        scheduler = mockk(relaxed = true)
        computeScheduledTriggerTime = mockk()
    }

    private fun useCase() = ScheduleAlarmUseCase(scheduler, computeScheduledTriggerTime)

    @Test
    fun `schedules alarm at computed trigger time`() = runBlocking {
        val pickedTime = 9_500_000L
        coEvery { computeScheduledTriggerTime(alarm) } returns pickedTime

        useCase()(alarm)

        coVerify(exactly = 1) { scheduler.scheduleAlarm(alarm.id, pickedTime) }
    }
}
