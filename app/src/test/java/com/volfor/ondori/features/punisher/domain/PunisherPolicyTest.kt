package com.volfor.ondori.features.punisher.domain

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PunisherPolicyTest {

    private lateinit var punisher: PunisherPolicy

    @Before
    fun setup() {
        punisher = PunisherPolicy()
    }

    @Test
    fun `withPenaltyOffset shifts time earlier by offset`() {
        val time = 10 * 60 * 1000L
        val offset = 3 * 60 * 1000L

        val result = punisher.withPenaltyOffset(time, offset)

        assertEquals(7 * 60 * 1000L, result)
    }

    @Test
    fun `withPenaltyOffset returns time unchanged when offset is zero`() {
        val time = 10 * 60 * 1000L

        val result = punisher.withPenaltyOffset(time, 0L)

        assertEquals(time, result)
    }
}
