package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ApplyPenaltyUseCaseTest {

    private lateinit var repo: PunisherRepository

    @Before
    fun setup() {
        repo = mockk(relaxed = true)
    }

    private fun useCase() = ApplyPenaltyUseCase(repo)

    @Test
    fun `applies penalty then clears last dismiss time`() = runBlocking {
        useCase()()

        coVerifyOrder {
            repo.applyPenalty()
            repo.setLastDismissedAlarmTime(null)
        }
    }
}
