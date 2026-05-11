package com.volfor.ondori.features.alarm.data.services

import com.volfor.ondori.features.alarm.domain.services.AlarmTimeCalculator
import java.time.Clock
import java.time.DayOfWeek
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmTimeCalculatorImpl @Inject constructor(
    private val clock: Clock
) : AlarmTimeCalculator {

    companion object {
        internal const val SAFETY_BUFFER_MINUTES = 30L
    }

    override fun computeNextTriggerTime(hour: Int, minute: Int, repeatDays: Set<DayOfWeek>): Long {
        val now = ZonedDateTime.now(clock)

//        val rules = zone.rules
//        val transition = rules.nextTransition(now.toInstant())
//
//        // Handle DST gap (e.g. 02:30 skipped)
//        if (!rules.isValidOffset(candidate.toLocalDateTime(), candidate.offset)) {
//            candidate = candidate.plusHours(1)
//        }

        if (repeatDays.isNotEmpty()) {
            for (dayOffset in 0..7) {
                val date = now.plusDays(dayOffset.toLong())
                if (date.dayOfWeek !in repeatDays) continue
                val trigger = date.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
                if (trigger.isAfter(now)) {
                    return trigger.toInstant().toEpochMilli()
                }
            }
        }

        var trigger = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

        if (!trigger.isAfter(now)) {
            trigger = trigger.plusDays(1)
        }

        return trigger.toInstant().toEpochMilli()
    }

    override fun computeSnoozeTriggerTime(snoozeMinutes: Int): Long {
        val minutes = snoozeMinutes.coerceAtLeast(1).toLong()
        val now = ZonedDateTime.now(clock)
        val trigger = now.plusMinutes(minutes).withSecond(0).withNano(0)
        return trigger.toInstant().toEpochMilli()
    }

    override fun pickSafeTriggerTime(preferred: Long, fallback: Long): Long {
        val earliestSafeTime = clock.millis() + SAFETY_BUFFER_MINUTES * 60L * 1000L
        return if (preferred >= earliestSafeTime) {
            preferred
        } else {
            fallback
        }
    }
}