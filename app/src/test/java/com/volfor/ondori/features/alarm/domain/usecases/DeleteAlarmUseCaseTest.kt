package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DeleteAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var scheduler: AlarmScheduler
    private lateinit var ringer: AlarmRinger

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        scheduler = mockk(relaxed = true)
        ringer = mockk(relaxed = true)
    }

    private fun useCase() = DeleteAlarmUseCase(repo, scheduler, ringer)

    @Test
    fun `stops ringing, cancels schedule and deletes alarm`() = runBlocking {
        val id = 9L

        useCase()(id)

        verify(exactly = 1) { ringer.stopRinging(id) }
        verify(exactly = 1) { scheduler.cancelAlarm(id) }
        coVerify(exactly = 1) { repo.deleteAlarm(id) }
    }
}
