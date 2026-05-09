package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class DisableAlarmUseCaseTest {

    private lateinit var repo: AlarmRepository
    private lateinit var cancelAlarm: CancelAlarmUseCase

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        cancelAlarm = mockk(relaxed = true)
    }

    private fun useCase() = DisableAlarmUseCase(repo, cancelAlarm)

    @Test
    fun `cancels alarm and persists disabled flag`() = runBlocking {
        val id = 44L

        useCase()(id)

        coVerify(exactly = 1) { cancelAlarm(id) }
        coVerify(exactly = 1) { repo.disableAlarm(id) }
    }
}
