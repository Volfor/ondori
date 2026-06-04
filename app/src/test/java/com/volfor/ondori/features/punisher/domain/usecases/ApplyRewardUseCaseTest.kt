package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class ApplyRewardUseCaseTest {

    private lateinit var repo: PunisherRepository

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
    }

    private fun fixedClockAt(millis: Long): Clock =
        Clock.fixed(Instant.ofEpochMilli(millis), ZoneOffset.UTC)

    @Test
    fun `applies reward then stamps current time as last dismiss`() = runBlocking {
        val now = 1_700_000_000_000L

        val useCase = ApplyRewardUseCase(repo, fixedClockAt(now))
        useCase()

        coVerifyOrder {
            repo.applyReward()
            repo.setLastDismissedAlarmTime(now)
        }
    }
}
