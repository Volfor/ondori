package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.DayOfWeek

class UpdateAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduleAlarm: ScheduleAlarmUseCase
    private lateinit var cancelAlarm: CancelAlarmUseCase
    private lateinit var checkDismissReversalForAlarm: CheckDismissReversalForAlarmUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
        cancelAlarm = mockk()
        checkDismissReversalForAlarm = mockk()
        rescheduleEnabledAlarms = mockk(relaxed = true)
        coEvery { repo.updateAlarm(any()) } just runs
        coEvery { scheduleAlarm(any()) } just runs
        coEvery { cancelAlarm(any()) } just runs
    }

    private fun useCase() = UpdateAlarmUseCase(
        repo,
        scheduleAlarm,
        cancelAlarm,
        checkDismissReversalForAlarm,
        rescheduleEnabledAlarms,
    )

    @Test
    fun `persists then schedules when alarm is enabled`() = runBlocking {
        val alarm = Alarm(
            id = 5L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.FRIDAY),
        )
        coEvery { repo.getAlarm(5L) } returns alarm

        useCase()(alarm)

        coVerifyOrder {
            repo.updateAlarm(alarm)
            scheduleAlarm(alarm)
        }
        coVerify(exactly = 0) { cancelAlarm(any()) }
        coVerify(exactly = 0) { checkDismissReversalForAlarm(any()) }
    }

    @Test
    fun `persists then cancels when alarm is disabled`() = runBlocking {
        val alarm = Alarm(
            id = 8L,
            hour = 7,
            minute = 0,
            enabled = false,
            repeatDays = setOf(DayOfWeek.FRIDAY),
        )
        coEvery { repo.getAlarm(8L) } returns alarm

        useCase()(alarm)

        coVerifyOrder {
            repo.updateAlarm(alarm)
            cancelAlarm(8L)
        }
        coVerify(exactly = 0) { scheduleAlarm(any()) }
        coVerify(exactly = 0) { checkDismissReversalForAlarm(any()) }
    }

    @Test
    fun `detects rescheduling when hour or minute changes`() = runBlocking {
        val previous = Alarm(id = 5L, hour = 7, minute = 0, enabled = true)
        val updated = previous.copy(hour = 7, minute = 30)
        coEvery { repo.getAlarm(5L) } returns previous
        coEvery { checkDismissReversalForAlarm(updated) } returns true

        useCase()(updated)

        coVerifyOrder {
            repo.updateAlarm(updated)
            checkDismissReversalForAlarm(updated)
            rescheduleEnabledAlarms()
            scheduleAlarm(updated)
        }
    }

    @Test
    fun `detects rescheduling when repeatDays change`() = runBlocking {
        val previous = Alarm(
            id = 5L,
            hour = 7,
            minute = 0,
            enabled = true,
            repeatDays = setOf(DayOfWeek.MONDAY),
        )
        val updated = previous.copy(repeatDays = setOf(DayOfWeek.TUESDAY))
        coEvery { repo.getAlarm(5L) } returns previous
        coEvery { checkDismissReversalForAlarm(updated) } returns true

        useCase()(updated)

        coVerify(exactly = 1) { checkDismissReversalForAlarm(updated) }
        coVerify(exactly = 1) { rescheduleEnabledAlarms() }
    }

    @Test
    fun `does not detect rescheduling when only label changes`() = runBlocking {
        val previous = Alarm(id = 5L, hour = 7, minute = 0, enabled = true, label = "old")
        val updated = previous.copy(label = "new")
        coEvery { repo.getAlarm(5L) } returns previous

        useCase()(updated)

        coVerify(exactly = 0) { checkDismissReversalForAlarm(any()) }
        coVerify(exactly = 1) { scheduleAlarm(updated) }
    }

    @Test
    fun `does not detect rescheduling when alarm is disabled`() = runBlocking {
        val previous = Alarm(id = 5L, hour = 7, minute = 0, enabled = true)
        val updated = previous.copy(hour = 8, enabled = false)
        coEvery { repo.getAlarm(5L) } returns previous

        useCase()(updated)

        coVerify(exactly = 0) { checkDismissReversalForAlarm(any()) }
        coVerify(exactly = 1) { cancelAlarm(5L) }
    }

    @Test
    fun `does not detect rescheduling when previous alarm not found`() = runBlocking {
        val updated = Alarm(id = 5L, hour = 8, minute = 0, enabled = true)
        coEvery { repo.getAlarm(5L) } returns null

        useCase()(updated)

        coVerify(exactly = 0) { checkDismissReversalForAlarm(any()) }
        coVerify(exactly = 1) { scheduleAlarm(updated) }
    }

    @Test
    fun `does not reschedule when time changes but no dismiss reversal detected`() = runBlocking {
        val previous = Alarm(id = 5L, hour = 7, minute = 0, enabled = true)
        val updated = previous.copy(hour = 8)
        coEvery { repo.getAlarm(5L) } returns previous
        coEvery { checkDismissReversalForAlarm(updated) } returns false

        useCase()(updated)

        coVerify(exactly = 1) { checkDismissReversalForAlarm(updated) }
        coVerify(exactly = 0) { rescheduleEnabledAlarms() }
        coVerify(exactly = 1) { scheduleAlarm(updated) }
    }
}
