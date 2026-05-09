package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.GetTimeWithPenaltyOffsetUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ScheduleAlarmUseCaseTest {

    private lateinit var scheduler: AlarmScheduler
    private lateinit var timeCalculator: AlarmTimeCalculator
    private lateinit var getTimeWithPenaltyOffset: GetTimeWithPenaltyOffsetUseCase

    private val alarm = Alarm(id = 1L, hour = 7, minute = 30, enabled = true)

    @Before
    fun setup() {
        scheduler = mockk(relaxed = true)
        timeCalculator = mockk()
        getTimeWithPenaltyOffset = mockk()
    }

    private fun useCase() =
        ScheduleAlarmUseCase(scheduler, timeCalculator, getTimeWithPenaltyOffset)

    @Test
    fun `schedules at the time picked by calculator from penalized and base times`() = runBlocking {
        val baseTime = 10_000_000L
        val penalizedTime = 9_500_000L
        val pickedTime = 9_500_000L

        every {
            timeCalculator.computeNextTriggerTime(alarm.hour, alarm.minute, alarm.repeatDays)
        } returns baseTime
        coEvery { getTimeWithPenaltyOffset(baseTime) } returns penalizedTime
        every { timeCalculator.pickSafeTriggerTime(penalizedTime, baseTime) } returns pickedTime

        useCase()(alarm)

        coVerify(exactly = 1) { scheduler.scheduleAlarm(alarm.id, pickedTime) }
    }

    @Test
    fun `falls back to base time when calculator rejects the penalized time`() = runBlocking {
        val baseTime = 10_000_000L
        val penalizedTime = 9_500_000L

        every {
            timeCalculator.computeNextTriggerTime(alarm.hour, alarm.minute, alarm.repeatDays)
        } returns baseTime
        coEvery { getTimeWithPenaltyOffset(baseTime) } returns penalizedTime
        every { timeCalculator.pickSafeTriggerTime(penalizedTime, baseTime) } returns baseTime

        useCase()(alarm)

        coVerify(exactly = 1) { scheduler.scheduleAlarm(alarm.id, baseTime) }
    }
}
