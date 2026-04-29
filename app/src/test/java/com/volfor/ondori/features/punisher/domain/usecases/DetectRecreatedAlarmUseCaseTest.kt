package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.PunisherPolicy
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class DetectRecreatedAlarmUseCaseTest {

    private lateinit var repo: PunisherRepository
    private lateinit var policy: PunisherPolicy

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
        policy = PunisherPolicy()
    }

    private fun fixedClockAt(millis: Long): Clock =
        Clock.fixed(Instant.ofEpochMilli(millis), ZoneOffset.UTC)

    @Test
    fun `does nothing when no last dismiss time stored`() = runBlocking {
        coEvery { repo.getLastDismissedAlarmTime() } returns null

        val useCase = DetectRecreatedAlarmUseCase(repo, policy, fixedClockAt(1_000L))
        useCase(newAlarmTriggerTime = 2_000L)

        coVerify(exactly = 0) { repo.applyRecreationPenalty() }
        coVerify(exactly = 0) { repo.setLastDismissedAlarmTime(any()) }
    }

    @Test
    fun `applies recreation penalty and clears timestamp when match`() = runBlocking {
        val dismissed = 1_000_000L
        val now = dismissed + 5 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L
        coEvery { repo.getLastDismissedAlarmTime() } returns dismissed

        val useCase = DetectRecreatedAlarmUseCase(repo, policy, fixedClockAt(now))
        useCase(newTrigger)

        coVerifyOrder {
            repo.applyRecreationPenalty()
            repo.setLastDismissedAlarmTime(null)
        }
    }

    @Test
    fun `does nothing when outside detection window`() = runBlocking {
        val dismissed = 1_000_000L
        val now = dismissed + 16 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L
        coEvery { repo.getLastDismissedAlarmTime() } returns dismissed

        val useCase = DetectRecreatedAlarmUseCase(repo, policy, fixedClockAt(now))
        useCase(newTrigger)

        coVerify(exactly = 0) { repo.applyRecreationPenalty() }
        coVerify(exactly = 0) { repo.setLastDismissedAlarmTime(any()) }
    }

    @Test
    fun `does nothing when new trigger past 30 min range`() = runBlocking {
        val dismissed = 1_000_000L
        val now = dismissed + 5 * 60 * 1000L
        val newTrigger = dismissed + 31 * 60 * 1000L
        coEvery { repo.getLastDismissedAlarmTime() } returns dismissed

        val useCase = DetectRecreatedAlarmUseCase(repo, policy, fixedClockAt(now))
        useCase(newTrigger)

        coVerify(exactly = 0) { repo.applyRecreationPenalty() }
        coVerify(exactly = 0) { repo.setLastDismissedAlarmTime(any()) }
    }
}
