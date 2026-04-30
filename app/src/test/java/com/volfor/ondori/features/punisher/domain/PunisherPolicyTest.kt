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

    @Test
    fun `withPenaltyOffset returns time unchanged when offset is negative`() {
        val time = 10 * 60 * 1000L

        val result = punisher.withPenaltyOffset(time, -5 * 60 * 1000L)

        assertEquals(time, result)
    }

    @Test
    fun `nextScoreAfterPenalty decrements when score is zero or negative`() {
        assertEquals(-1, punisher.nextScoreAfterPenalty(0))
        assertEquals(-2, punisher.nextScoreAfterPenalty(-1))
        assertEquals(-13, punisher.nextScoreAfterPenalty(-12))
    }

    @Test
    fun `nextScoreAfterPenalty collapses any positive streak to -1`() {
        // Single penalty wipes the entire positive streak.
        assertEquals(-1, punisher.nextScoreAfterPenalty(1))
        assertEquals(-1, punisher.nextScoreAfterPenalty(5))
        assertEquals(-1, punisher.nextScoreAfterPenalty(30))
    }

    @Test
    fun `nextScoreAfterReward increments score`() {
        assertEquals(-4, punisher.nextScoreAfterReward(-5))
        assertEquals(0, punisher.nextScoreAfterReward(-1))
        assertEquals(1, punisher.nextScoreAfterReward(0))
        assertEquals(6, punisher.nextScoreAfterReward(5))
    }

    @Test
    fun `nextScoreAfterDismissReversal reverts reward then applies penalty step`() {
        // input is the post-reward score (after dismiss)
        assertEquals(-1, punisher.nextScoreAfterDismissReversal(6)) // pre-dismiss 5
        assertEquals(-1, punisher.nextScoreAfterDismissReversal(1)) // pre-dismiss 0
        assertEquals(-2, punisher.nextScoreAfterDismissReversal(0)) // pre-dismiss -1
        assertEquals(-3, punisher.nextScoreAfterDismissReversal(-1)) // pre-dismiss -2
    }

    @Test
    fun `penaltyOffsetMillis is zero for non-negative scores`() {
        assertEquals(0L, punisher.penaltyOffsetMillis(0))
        assertEquals(0L, punisher.penaltyOffsetMillis(1))
        assertEquals(0L, punisher.penaltyOffsetMillis(30))
    }

    @Test
    fun `penaltyOffsetMillis maps each negative tier to its offset`() {
        assertEquals(5 * 60 * 1000L, punisher.penaltyOffsetMillis(-1))
        assertEquals(10 * 60 * 1000L, punisher.penaltyOffsetMillis(-2))
        assertEquals(15 * 60 * 1000L, punisher.penaltyOffsetMillis(-3))
        assertEquals(15 * 60 * 1000L, punisher.penaltyOffsetMillis(-4))
        assertEquals(20 * 60 * 1000L, punisher.penaltyOffsetMillis(-5))
        assertEquals(20 * 60 * 1000L, punisher.penaltyOffsetMillis(-6))
        assertEquals(30 * 60 * 1000L, punisher.penaltyOffsetMillis(-7))
        assertEquals(30 * 60 * 1000L, punisher.penaltyOffsetMillis(-8))
        assertEquals(45 * 60 * 1000L, punisher.penaltyOffsetMillis(-9))
        assertEquals(45 * 60 * 1000L, punisher.penaltyOffsetMillis(-12))
        assertEquals(60 * 60 * 1000L, punisher.penaltyOffsetMillis(-13))
        assertEquals(60 * 60 * 1000L, punisher.penaltyOffsetMillis(Int.MIN_VALUE))
    }

    @Test
    fun `isDismissReversal true within window and trigger range`() {
        val dismissed = 1_000_000L
        val now = dismissed + 5 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L

        assertEquals(true, punisher.isDismissReversal(dismissed, now, newTrigger))
    }

    @Test
    fun `isDismissReversal false when now past detection window`() {
        val dismissed = 1_000_000L
        val now = dismissed + 16 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L

        assertEquals(false, punisher.isDismissReversal(dismissed, now, newTrigger))
    }

    @Test
    fun `isDismissReversal false when trigger before dismiss`() {
        val dismissed = 1_000_000L
        val now = dismissed + 1 * 60 * 1000L
        val newTrigger = dismissed - 1L

        assertEquals(false, punisher.isDismissReversal(dismissed, now, newTrigger))
    }

    @Test
    fun `isDismissReversal false when trigger past 30 min range`() {
        val dismissed = 1_000_000L
        val now = dismissed + 1 * 60 * 1000L
        val newTrigger = dismissed + 31 * 60 * 1000L

        assertEquals(false, punisher.isDismissReversal(dismissed, now, newTrigger))
    }

    @Test
    fun `isDismissReversal true at boundaries`() {
        val dismissed = 1_000_000L
        val nowEdge = dismissed + 15 * 60 * 1000L
        val triggerEdge = dismissed + 30 * 60 * 1000L

        assertEquals(true, punisher.isDismissReversal(dismissed, nowEdge, triggerEdge))
        assertEquals(true, punisher.isDismissReversal(dismissed, dismissed, dismissed))
    }
}
