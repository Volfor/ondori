package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.services.AlarmRinger
import com.volfor.ondori.features.alarm.domain.services.AlarmScheduler
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CancelAlarmUseCaseTest {

    private lateinit var scheduler: AlarmScheduler
    private lateinit var ringer: AlarmRinger

    @Before
    fun setup() {
        scheduler = mockk(relaxed = true)
        ringer = mockk(relaxed = true)
    }

    private fun useCase() = CancelAlarmUseCase(scheduler, ringer)

    @Test
    fun `stops ringing and cancels scheduled alarm`() = runBlocking {
        val id = 12L

        useCase()(id)

        verify(exactly = 1) { ringer.stopRinging(id) }
        verify(exactly = 1) { scheduler.cancelAlarm(id) }
    }
}
