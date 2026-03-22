package com.volfor.ondori.features.alarm.domain.services

import java.time.Clock
import java.time.DayOfWeek
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmTimeCalculator @Inject constructor(
    private val clock: Clock
) {

    companion object {
        internal const val SNOOZE_MINUTES = 1 // TODO: allow to modify this value for each alarm
    }

    fun computeNextTriggerTime(hour: Int, minute: Int, repeatDays: Set<DayOfWeek>): Long {
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

    fun computeSnoozeTriggerTime(): Long {
        val now = ZonedDateTime.now(clock)
        val trigger = now.plusMinutes(SNOOZE_MINUTES.toLong()).withSecond(0).withNano(0)
        return trigger.toInstant().toEpochMilli()
    }
}