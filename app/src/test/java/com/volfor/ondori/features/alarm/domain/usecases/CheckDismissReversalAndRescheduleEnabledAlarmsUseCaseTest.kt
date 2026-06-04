package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import com.volfor.ondori.features.punisher.domain.usecases.DetectDismissReversalUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CheckDismissReversalAndRescheduleEnabledAlarmsUseCaseTest {

    private lateinit var timeCalculator: AlarmTimeCalculator
    private lateinit var detectDismissReversal: DetectDismissReversalUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    private val alarm = Alarm(id = 1L, hour = 8, minute = 30, enabled = true)
    private val triggerTime = 1_700_000_000_000L

    @Before
    fun setup() {
        timeCalculator = mockk()
        detectDismissReversal = mockk()
        rescheduleEnabledAlarms = mockk(relaxed = true)
        every {
            timeCalculator.computeNextTriggerTime(
                hour = alarm.hour,
                minute = alarm.minute,
                repeatDays = alarm.repeatDays,
            )
        } returns triggerTime
    }

    private fun useCase() = CheckDismissReversalAndRescheduleEnabledAlarmsUseCase(
        timeCalculator,
        detectDismissReversal,
        rescheduleEnabledAlarms,
    )

    @Test
    fun `reschedules enabled alarms when dismiss reversal is detected`() = runBlocking {
        coEvery { detectDismissReversal(triggerTime) } returns true

        useCase()(alarm)

        coVerify(exactly = 1) { detectDismissReversal(triggerTime) }
        coVerify(exactly = 1) { rescheduleEnabledAlarms() }
    }

    @Test
    fun `does not reschedule when dismiss reversal is not detected`() = runBlocking {
        coEvery { detectDismissReversal(triggerTime) } returns false

        useCase()(alarm)

        coVerify(exactly = 1) { detectDismissReversal(triggerTime) }
        coVerify(exactly = 0) { rescheduleEnabledAlarms() }
    }
}
