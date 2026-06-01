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

class EnableAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduleAlarm: ScheduleAlarmUseCase
    private lateinit var checkDismissReversalForAlarm: CheckDismissReversalForAlarmUseCase
    private lateinit var rescheduleEnabledAlarms: RescheduleEnabledAlarmsUseCase

    @Before
    fun setup() {
        repo = mockk()
        scheduleAlarm = mockk()
        checkDismissReversalForAlarm = mockk()
        rescheduleEnabledAlarms = mockk(relaxed = true)
    }

    private fun useCase() =
        EnableAlarmUseCase(repo, scheduleAlarm, checkDismissReversalForAlarm, rescheduleEnabledAlarms)

    @Test
    fun `does nothing when alarm is missing`() = runBlocking {
        val id = -1L
        coEvery { repo.getAlarm(id) } returns null

        useCase()(id)

        coVerify(exactly = 0) { repo.enableAlarm(any()) }
        coVerify(exactly = 0) { scheduleAlarm(any()) }
        coVerify(exactly = 0) { checkDismissReversalForAlarm(any()) }
    }

    @Test
    fun `loads alarm, enables then schedules with repository alarm`() = runBlocking {
        val id = 7L
        val fromRepo = Alarm(
            id = id,
            hour = 9,
            minute = 15,
            enabled = false,
            repeatDays = setOf(DayOfWeek.WEDNESDAY),
            label = "from db",
        )
        coEvery { repo.getAlarm(id) } returns fromRepo
        coEvery { repo.enableAlarm(id) } just runs
        coEvery { checkDismissReversalForAlarm(fromRepo) } returns false
        coEvery { scheduleAlarm(any()) } just runs

        useCase()(id)

        coVerify(exactly = 1) { repo.getAlarm(id) }
        coVerify(exactly = 1) { repo.enableAlarm(fromRepo.id) }
        coVerify(exactly = 1) { scheduleAlarm(fromRepo) }
        coVerify(exactly = 0) { rescheduleEnabledAlarms() }
    }

    @Test
    fun `reschedules enabled alarms when dismiss reversal detected`() = runBlocking {
        val id = 7L
        val fromRepo = Alarm(
            id = id,
            hour = 9,
            minute = 15,
            enabled = false,
        )
        coEvery { repo.getAlarm(id) } returns fromRepo
        coEvery { repo.enableAlarm(id) } just runs
        coEvery { checkDismissReversalForAlarm(fromRepo) } returns true
        coEvery { scheduleAlarm(any()) } just runs

        useCase()(id)

        coVerifyOrder {
            repo.enableAlarm(fromRepo.id)
            checkDismissReversalForAlarm(fromRepo)
            rescheduleEnabledAlarms()
            scheduleAlarm(fromRepo)
        }
    }
}
