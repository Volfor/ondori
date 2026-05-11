package com.volfor.ondori.features.alarm.data.services

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class AlarmTimeCalculatorImplTest {

    private val zone = ZoneId.of("UTC")

    @Test
    fun `schedules alarm for today when time is in the future`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(11, 0, emptySet())
        val expected = Instant.parse("2026-03-11T11:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `schedules alarm for tomorrow when time already passed`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(9, 0, emptySet())
        val expected = Instant.parse("2026-03-12T09:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `schedules alarm for tomorrow when time equals current time`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(10, 0, emptySet())
        val expected = Instant.parse("2026-03-12T10:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `correctly handles minute precision`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:15:35Z"), zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(10, 20, emptySet())
        val expected = Instant.parse("2026-03-11T10:20:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `alarm at 23_59 schedules for today when current time is 23_58_59`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T23:58:59Z"), zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(23, 59, emptySet())
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
            val calculator = AlarmTimeCalculatorImpl(clock)

            val result = calculator.computeNextTriggerTime(0, 0, emptySet())
            val expected = Instant.parse("2026-03-12T00:00:00Z").toEpochMilli()

            assertEquals(expected, result)
        }
    }

    @Test
    fun `respects non-UTC timezone for scheduling`() {
        val zone = ZoneId.of("Asia/Tokyo") // UTC+9, no DST
        val clock = Clock.fixed(Instant.parse("2026-03-11T02:00:00Z"), zone) // 11:00 JST
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(12, 0, emptySet())
        val expected = Instant.parse("2026-03-11T03:00:00Z").toEpochMilli() // 12:00 JST

        assertEquals(expected, result)
    }

    @Test
    fun `handles DST forward transition`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(Instant.parse("2026-03-29T00:30:00Z"), zone) // Local 01:30, UTC+1
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(3, 30, emptySet()) // Alarm for 03:30
        val expected =
            Instant.parse("2026-03-29T01:30:00Z").toEpochMilli() // Local 03:30 after DST, UTC+2

        assertEquals(expected, result)
    }

    @Test
    fun `nonexistent DST alarm time is normalized to 03_30`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(Instant.parse("2026-03-29T00:30:00Z"), zone)  // Local 01:30, UTC+1
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(2, 30, emptySet()) // Alarm for 02:30
        val expected =
            Instant.parse("2026-03-29T01:30:00Z").toEpochMilli() // Local 03:30 after DST, UTC+2

        assertEquals(expected, result)
    }

    @Test
    fun `handles DST backward transition`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(Instant.parse("2026-10-25T00:30:00Z"), zone) // Local 02:30, UTC+2
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(3, 30, emptySet()) // Alarm for 03:30
        val expected =
            Instant.parse("2026-10-25T02:30:00Z").toEpochMilli() // Local 03:30 after DST, UTC+1

        assertEquals(expected, result)
    }

    @Test
    fun `schedules next day when repeated DST hour already passed`() {
        val zone = ZoneId.of("Europe/Warsaw")
        val clock = Clock.fixed(
            Instant.parse("2026-10-25T02:30:00Z"), zone
        ) // Local 03:30 25.10, after DST, UTC+1
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(2, 30, emptySet()) // Alarm for 02:30
        val expected =
            Instant.parse("2026-10-26T01:30:00Z").toEpochMilli() // Local 02:30 26.10, UTC+1

        assertEquals(expected, result)
    }

    @Test
    fun `snooze adds requested minutes`() {
        val base = Instant.parse("2026-03-11T10:00:00Z")
        val clock = Clock.fixed(base, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeSnoozeTriggerTime(15)
        val expected = base.plus(15L, ChronoUnit.MINUTES).toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `snooze at 23_59 rolls to next day`() {
        val base = Instant.parse("2026-03-11T23:59:00Z")
        val clock = Clock.fixed(base, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeSnoozeTriggerTime(1)
        val expected = base.plus(1L, ChronoUnit.MINUTES).toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `snooze rounds to next full minute`() {
        val base = Instant.parse("2026-03-11T10:15:35Z")
        val clock = Clock.fixed(base, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeSnoozeTriggerTime(1)
        val expected =
            base.plus(1L, ChronoUnit.MINUTES).truncatedTo(ChronoUnit.MINUTES).toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `repeat schedules same day when weekday matches and time is still ahead`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T08:00:00Z"), zone) // Wed
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(9, 0, setOf(DayOfWeek.WEDNESDAY))
        val expected = Instant.parse("2026-03-11T09:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `repeat rolls to next week when today's slot already passed`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T15:00:00Z"), zone) // Wed
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(9, 0, setOf(DayOfWeek.WEDNESDAY))
        val expected = Instant.parse("2026-03-18T09:00:00Z").toEpochMilli() // next Wed

        assertEquals(expected, result)
    }

    @Test
    fun `repeat skips to next selected weekday`() {
        val clock = Clock.fixed(Instant.parse("2026-03-09T10:00:00Z"), zone) // Mon
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(9, 0, setOf(DayOfWeek.WEDNESDAY))
        val expected = Instant.parse("2026-03-11T09:00:00Z").toEpochMilli() // Wed

        assertEquals(expected, result)
    }

    @Test
    fun `repeat picks earliest matching day when several weekdays selected`() {
        val clock = Clock.fixed(Instant.parse("2026-03-09T10:00:00Z"), zone) // Mon
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(
            9,
            0,
            setOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
        )
        val expected = Instant.parse("2026-03-10T09:00:00Z").toEpochMilli() // Tue before Wed

        assertEquals(expected, result)
    }

    @Test
    fun `repeat when now equals alarm time moves to next matching weekday`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T09:00:00Z"), zone) // Wed 09:00
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(
            9,
            0,
            setOf(DayOfWeek.WEDNESDAY),
        )
        val expected = Instant.parse("2026-03-18T09:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `empty repeatDays uses daily next today or tomorrow behavior`() {
        val clock = Clock.fixed(Instant.parse("2026-03-11T10:00:00Z"), zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(11, 0, emptySet())
        val expected = Instant.parse("2026-03-11T11:00:00Z").toEpochMilli()

        assertEquals(expected, result)
    }

    @Test
    fun `repeat respects clock zone for weekday boundaries`() {
        val tokyo = ZoneId.of("Asia/Tokyo")
        val clock = Clock.fixed(Instant.parse("2026-03-09T02:00:00Z"), tokyo) // Mon 11:00 JST
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(
            9,
            0,
            setOf(DayOfWeek.WEDNESDAY),
        )
        val expected = Instant.parse("2026-03-11T00:00:00Z").toEpochMilli() // Wed 09:00 JST

        assertEquals(expected, result)
    }

    @Test
    fun `repeat schedules next selected weekday when earlier weekday already passed`() {
        val clock = Clock.fixed(Instant.parse("2026-03-10T15:00:00Z"), zone) // Tue 15:00
        val calculator = AlarmTimeCalculatorImpl(clock)

        val result = calculator.computeNextTriggerTime(
            9,
            0,
            setOf(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
        )
        val expected = Instant.parse("2026-03-11T09:00:00Z").toEpochMilli() // Wed 09:00

        assertEquals(expected, result)
    }

    @Test
    fun `pickSafeTriggerTime returns preferred when it is beyond the safety buffer`() {
        val now = Instant.parse("2026-03-11T10:00:00Z")
        val clock = Clock.fixed(now, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val preferred = now.plus(1, ChronoUnit.HOURS).toEpochMilli()
        val fallback = now.plus(2, ChronoUnit.HOURS).toEpochMilli()

        val result = calculator.pickSafeTriggerTime(preferred, fallback)

        assertEquals(preferred, result)
    }

    @Test
    fun `pickSafeTriggerTime returns preferred when it equals the safety buffer boundary`() {
        val now = Instant.parse("2026-03-11T10:00:00Z")
        val clock = Clock.fixed(now, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val preferred = now.plus(
            AlarmTimeCalculatorImpl.SAFETY_BUFFER_MINUTES,
            ChronoUnit.MINUTES,
        ).toEpochMilli()
        val fallback = now.plus(5, ChronoUnit.HOURS).toEpochMilli()

        val result = calculator.pickSafeTriggerTime(preferred, fallback)

        assertEquals(preferred, result)
    }

    @Test
    fun `pickSafeTriggerTime returns fallback when preferred is inside the safety buffer`() {
        val now = Instant.parse("2026-03-11T10:00:00Z")
        val clock = Clock.fixed(now, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val preferred = now.plus(
            AlarmTimeCalculatorImpl.SAFETY_BUFFER_MINUTES - 1,
            ChronoUnit.MINUTES,
        ).toEpochMilli()
        val fallback = now.plus(5, ChronoUnit.HOURS).toEpochMilli()

        val result = calculator.pickSafeTriggerTime(preferred, fallback)

        assertEquals(fallback, result)
    }

    @Test
    fun `pickSafeTriggerTime returns fallback when preferred is in the past`() {
        val now = Instant.parse("2026-03-11T10:00:00Z")
        val clock = Clock.fixed(now, zone)
        val calculator = AlarmTimeCalculatorImpl(clock)

        val preferred = now.minus(10, ChronoUnit.MINUTES).toEpochMilli()
        val fallback = now.plus(5, ChronoUnit.HOURS).toEpochMilli()

        val result = calculator.pickSafeTriggerTime(preferred, fallback)

        assertEquals(fallback, result)
    }
}