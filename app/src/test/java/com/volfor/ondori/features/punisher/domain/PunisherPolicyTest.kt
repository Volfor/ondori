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
    fun `nextScoreAfterRecreation reverts reward then applies penalty step`() {
        // input is the post-reward score (after dismiss)
        assertEquals(-1, punisher.nextScoreAfterRecreation(6)) // pre-dismiss 5
        assertEquals(-1, punisher.nextScoreAfterRecreation(1)) // pre-dismiss 0
        assertEquals(-3, punisher.nextScoreAfterRecreation(-1)) // pre-dismiss -2
    }

    @Test
    fun `isRecreatedAlarm true within window and trigger range`() {
        val dismissed = 1_000_000L
        val now = dismissed + 5 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L

        assertEquals(true, punisher.isRecreatedAlarm(dismissed, now, newTrigger))
    }

    @Test
    fun `isRecreatedAlarm false when now past detection window`() {
        val dismissed = 1_000_000L
        val now = dismissed + 16 * 60 * 1000L
        val newTrigger = dismissed + 10 * 60 * 1000L

        assertEquals(false, punisher.isRecreatedAlarm(dismissed, now, newTrigger))
    }

    @Test
    fun `isRecreatedAlarm false when trigger before dismiss`() {
        val dismissed = 1_000_000L
        val now = dismissed + 1 * 60 * 1000L
        val newTrigger = dismissed - 1L

        assertEquals(false, punisher.isRecreatedAlarm(dismissed, now, newTrigger))
    }

    @Test
    fun `isRecreatedAlarm false when trigger past 30 min range`() {
        val dismissed = 1_000_000L
        val now = dismissed + 1 * 60 * 1000L
        val newTrigger = dismissed + 31 * 60 * 1000L

        assertEquals(false, punisher.isRecreatedAlarm(dismissed, now, newTrigger))
    }

    @Test
    fun `isRecreatedAlarm true at boundaries`() {
        val dismissed = 1_000_000L
        val nowEdge = dismissed + 15 * 60 * 1000L
        val triggerEdge = dismissed + 30 * 60 * 1000L

        assertEquals(true, punisher.isRecreatedAlarm(dismissed, nowEdge, triggerEdge))
        assertEquals(true, punisher.isRecreatedAlarm(dismissed, dismissed, dismissed))
    }
}
