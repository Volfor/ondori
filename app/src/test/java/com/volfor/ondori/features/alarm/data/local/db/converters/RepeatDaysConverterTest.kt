package com.volfor.ondori.features.alarm.data.local.db.converters

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek

class RepeatDaysConverterTest {

    private val converter = RepeatDaysConverter()

    @Test
    fun `empty set encodes to zero and decodes back`() {
        assertEquals(0, converter.fromDays(emptySet()))
        assertEquals(emptySet<DayOfWeek>(), converter.toDays(0))
    }

    @Test
    fun `hand-picked single-day masks match java time DayOfWeek values`() {
        assertEquals(1, converter.fromDays(setOf(DayOfWeek.MONDAY)))
        assertEquals(2, converter.fromDays(setOf(DayOfWeek.TUESDAY)))
        assertEquals(4, converter.fromDays(setOf(DayOfWeek.WEDNESDAY)))
        assertEquals(8, converter.fromDays(setOf(DayOfWeek.THURSDAY)))
        assertEquals(16, converter.fromDays(setOf(DayOfWeek.FRIDAY)))
        assertEquals(32, converter.fromDays(setOf(DayOfWeek.SATURDAY)))
        assertEquals(64, converter.fromDays(setOf(DayOfWeek.SUNDAY)))
    }

    @Test
    fun `hand-picked single-day masks decode to expected days ordering Mon to Sun`() {
        assertEquals(setOf(DayOfWeek.MONDAY), converter.toDays(1))
        assertEquals(setOf(DayOfWeek.TUESDAY), converter.toDays(2))
        assertEquals(setOf(DayOfWeek.WEDNESDAY), converter.toDays(4))
        assertEquals(setOf(DayOfWeek.THURSDAY), converter.toDays(8))
        assertEquals(setOf(DayOfWeek.FRIDAY), converter.toDays(16))
        assertEquals(setOf(DayOfWeek.SATURDAY), converter.toDays(32))
        assertEquals(setOf(DayOfWeek.SUNDAY), converter.toDays(64))
    }

    @Test
    fun `full week mask is 127 and round-trips`() {
        val allDays = DayOfWeek.entries.toSet()
        assertEquals(127, converter.fromDays(allDays))
        assertEquals(allDays, converter.toDays(127))
    }

    @Test
    fun `weekend pair matches expected bitmask`() {
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        assertEquals(96, converter.fromDays(weekend))
        assertEquals(weekend, converter.toDays(96))
    }

    @Test
    fun `weekdays mask avoids off-by-one at Sunday boundary`() {
        val weekdays = DayOfWeek.entries.filter { it != DayOfWeek.SATURDAY && it != DayOfWeek.SUNDAY }.toSet()
        assertEquals(31, converter.fromDays(weekdays))
        assertEquals(weekdays, converter.toDays(31))
    }

    @Test
    fun `decode encode round-trip for every mask 0 through 127`() {
        for (mask in 0..127) {
            val days = converter.toDays(mask)
            assertEquals(mask, converter.fromDays(days))
        }
    }

    @Test
    fun `encode decode round-trip for every subset of days`() {
        val all = DayOfWeek.entries
        for (k in 0..all.size) {
            generateCombinations(all, k).forEach { subset ->
                val set = subset.toSet()
                assertEquals(set, converter.toDays(converter.fromDays(set)))
            }
        }
    }

    @Test
    fun `encoding is order independent`() {
        val days = setOf(DayOfWeek.FRIDAY, DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val reversed = days.reversed().toSet()
        assertEquals(converter.fromDays(days), converter.fromDays(reversed))
    }

    private fun <T> generateCombinations(items: List<T>, k: Int): Sequence<List<T>> = sequence {
        if (k == 0) {
            yield(emptyList())
            return@sequence
        }
        if (items.size < k) return@sequence
        val head = items.first()
        val tail = items.drop(1)
        yieldAll(generateCombinations(tail, k - 1).map { listOf(head) + it })
        yieldAll(generateCombinations(tail, k))
    }
}
