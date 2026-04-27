package com.volfor.ondori.features.punisher.domain.usecases

import com.volfor.ondori.features.punisher.domain.PunisherPolicy
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetTimeWithPenaltyOffsetUseCaseTest {

    private lateinit var repo: PunisherRepository
    private lateinit var policy: PunisherPolicy

    @Before
    fun setup() {
        repo = mockk()
        policy = PunisherPolicy()
    }

    @Test
    fun `composes score, policy offset, and policy time shift`() = runBlocking {
        coEvery { repo.getScore() } returns -3

        val time = 10_000_000L
        val offset = 15 * 60 * 1000L

        val useCase = GetTimeWithPenaltyOffsetUseCase(repo, policy)
        val result = useCase(time)

        assertEquals(time - offset, result)
    }

    @Test
    fun `returns time unchanged when score is 0`() = runBlocking {
        coEvery { repo.getScore() } returns 0

        val time = 10_000_000L

        val useCase = GetTimeWithPenaltyOffsetUseCase(repo, policy)
        val result = useCase(time)

        assertEquals(time, result)
    }

    @Test
    fun `returns time unchanged when score is positive`() = runBlocking {
        coEvery { repo.getScore() } returns 5

        val time = 10_000_000L

        val useCase = GetTimeWithPenaltyOffsetUseCase(repo, policy)
        val result = useCase(time)

        assertEquals(time, result)
    }
}
