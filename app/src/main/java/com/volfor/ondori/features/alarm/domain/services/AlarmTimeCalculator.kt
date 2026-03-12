package com.volfor.ondori.features.alarm.domain.services

import java.time.Clock
import java.time.ZonedDateTime
import javax.inject.Inject

class AlarmTimeCalculator @Inject constructor(
    private val clock: Clock
) {

    fun computeNextTriggerTime(hour: Int, minute: Int): Long {
        val now = ZonedDateTime.now(clock)

        var trigger = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

        if (!trigger.isAfter(now)) {
            trigger = trigger.plusDays(1)
        }

//        val rules = zone.rules
//        val transition = rules.nextTransition(now.toInstant())
//
//        // Handle DST gap (e.g. 02:30 skipped)
//        if (!rules.isValidOffset(candidate.toLocalDateTime(), candidate.offset)) {
//            candidate = candidate.plusHours(1)
//        }

        return trigger.toInstant().toEpochMilli()
    }
}