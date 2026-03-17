package com.volfor.ondori.features.alarm.domain.services

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class AlarmTimeCalculatorTest {

    private val zone = ZoneId.of("UTC")

    @Test
    fun `schedules alarm for today when time is in the future`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(11, 0)
        val expected = Instant.parse("2026-03-11T11:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `schedules alarm for tomorrow when time already passed`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(9, 0)
        val expected = Instant.parse("2026-03-12T09:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `schedules alarm for tomorrow when time equals current time`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(10, 0)
        val expected = Instant.parse("2026-03-12T10:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `correctly handles minute precision`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:15:35Z"), zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(10, 20)
        val expected = Instant.parse("2026-03-11T10:20:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `alarm at 23_59 schedules for today when current time is 23_58_59`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T23:58:59Z"), zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(23, 59)
        val expected = Instant.parse("2026-03-11T23:59:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `midnight alarm is always scheduled for next day`() {
        val testTimes = listOf(
            "2026-03-11T00:00:00Z",
            "2026-03-11T00:01:00Z",
            "2026-03-11T12:00:00Z",
            "2026-03-11T23:59:00Z"
        )

        testTimes.forEach {
            val clock = Clock.fixed(Instant.parse(it), zone)
            val calculator = AlarmTimeCalculator(clock)

            val result = calculator.computeNextTriggerTime(0, 0)
            val expected = Instant.parse("2026-03-12T00:00:00Z").toEpochMilli()

            assertEquals(expected, result)
        }
    }

    @Test
    fun `respects non-UTC timezone for scheduling`() {
        val zone = ZoneId.of("Asia/Tokyo") // UTC+9, no DST
        val clock = Clock.fixed(Instant.parse("2026-03-11T02:00:00Z"), zone) // 11:00 JST
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(12, 0)
        val expected = Instant.parse("2026-03-11T03:00:00Z").toEpochMilli() // 12:00 JST

        assertEquals(expected, result)
    }

    @Test
    fun `handles DST forward transition`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(Instant.parse("2026-03-29T00:30:00Z"), zone) // Local 01:30, UTC+1
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(3, 30) // Alarm for 03:30
        val expected =
            Instant.parse("2026-03-29T01:30:00Z").toEpochMilli() // Local 03:30 after DST, UTC+2

        assertEquals(expected, result)
    }

    @Test
    fun `nonexistent DST alarm time is normalized to 03_30`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(Instant.parse("2026-03-29T00:30:00Z"), zone)  // Local 01:30, UTC+1
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(2, 30) // Alarm for 02:30
        val expected =
            Instant.parse("2026-03-29T01:30:00Z").toEpochMilli() // Local 03:30 after DST, UTC+2

        assertEquals(expected, result)
    }

    @Test
    fun `handles DST backward transition`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(Instant.parse("2026-10-25T00:30:00Z"), zone) // Local 02:30, UTC+2
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(3, 30) // Alarm for 03:30
        val expected =
            Instant.parse("2026-10-25T02:30:00Z").toEpochMilli() // Local 03:30 after DST, UTC+1

        assertEquals(expected, result)
    }

    @Test
    fun `schedules next day when repeated DST hour already passed`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(
            Instant.parse("2026-10-25T02:30:00Z"),
            zone
        ) // Local 03:30 25.10, after DST, UTC+1
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeNextTriggerTime(2, 30) // Alarm for 02:30
        val expected =
            Instant.parse("2026-10-26T01:30:00Z").toEpochMilli() // Local 02:30 26.10, UTC+1

        assertEquals(expected, result)
    }

    @Test
    fun `snooze adds one minute to current time`() {
        val base = Instant.parse("2026-03-11T10:00:00Z")
        val clock = Clock.fixed(base, zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeSnoozeTriggerTime()
        val expected = base.plus(AlarmTimeCalculator.SNOOZE_MINUTES.toLong(), ChronoUnit.MINUTES).toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `snooze at 23_59 rolls to next day`() {
        val base = Instant.parse("2026-03-11T23:59:00Z")
        val clock = Clock.fixed(base, zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeSnoozeTriggerTime()
        val expected = base.plus(AlarmTimeCalculator.SNOOZE_MINUTES.toLong(), ChronoUnit.MINUTES).toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `snooze rounds to next full minute`() {
        val base = Instant.parse("2026-03-11T10:15:35Z")
        val clock = Clock.fixed(base, zone)
        val calculator = AlarmTimeCalculator(clock)

        val result = calculator.computeSnoozeTriggerTime()
        val expected = base
            .plus(AlarmTimeCalculator.SNOOZE_MINUTES.toLong(), ChronoUnit.MINUTES)
            .truncatedTo(ChronoUnit.MINUTES)
            .toEpochMilli()

        assertEquals(expected, result)
    }
}