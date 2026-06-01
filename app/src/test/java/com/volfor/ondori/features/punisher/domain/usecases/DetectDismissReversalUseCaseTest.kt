package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.PunisherPolicy
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class DetectDismissReversalUseCaseTest {

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

        val useCase = DetectDismissReversalUseCase(repo, policy, fixedClockAt(1_000L))
        val result = useCase(newAlarmTriggerTime = 2_000L)

        assertFalse(result)
        coVerify(exactly = 0) { repo.applyDismissReversalPenalty() }
        coVerify(exactly = 0) { repo.setLastDismissedAlarmTime(any()) }
    }

    @Test
    fun `applies dismiss reversal penalty and clears timestamp when match`() = runBlocking {
        val dismissed = 1_000_000L
        val now = dismissed + 5 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L
        coEvery { repo.getLastDismissedAlarmTime() } returns dismissed

        val useCase = DetectDismissReversalUseCase(repo, policy, fixedClockAt(now))
        val result = useCase(newTrigger)

        assertTrue(result)
        coVerifyOrder {
            repo.applyDismissReversalPenalty()
            repo.setLastDismissedAlarmTime(null)
        }
    }

    @Test
    fun `does nothing when outside detection window`() = runBlocking {
        val dismissed = 1_000_000L
        val now = dismissed + 16 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L
        coEvery { repo.getLastDismissedAlarmTime() } returns dismissed

        val useCase = DetectDismissReversalUseCase(repo, policy, fixedClockAt(now))
        val result = useCase(newTrigger)

        assertFalse(result)
        coVerify(exactly = 0) { repo.applyDismissReversalPenalty() }
        coVerify(exactly = 0) { repo.setLastDismissedAlarmTime(any()) }
    }

    @Test
    fun `does nothing when new trigger past 30 min range`() = runBlocking {
        val dismissed = 1_000_000L
        val now = dismissed + 5 * 60 * 1000L
        val newTrigger = dismissed + 31 * 60 * 1000L
        coEvery { repo.getLastDismissedAlarmTime() } returns dismissed

        val useCase = DetectDismissReversalUseCase(repo, policy, fixedClockAt(now))
        val result = useCase(newTrigger)

        assertFalse(result)
        coVerify(exactly = 0) { repo.applyDismissReversalPenalty() }
        coVerify(exactly = 0) { repo.setLastDismissedAlarmTime(any()) }
    }
}
