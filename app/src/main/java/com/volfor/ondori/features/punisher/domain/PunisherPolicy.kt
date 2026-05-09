package com.volfor.ondori.features.punisher.domain

import com.volfor.ondori.features.punisher.domain.entities.PenaltyLevel
import javax.inject.Inject

class PunisherPolicy @Inject constructor() {

    companion object {
        /**
         * Time window after dismiss during which a newly created alarm is treated
         * as a "recreated" alarm.
         */
        private const val RECREATION_DETECTION_WINDOW_MILLIS: Long = 15L * 60L * 1000L

        /**
         * Range above the dismiss time within which a new alarm trigger time qualifies
         * as a recreation of the dismissed alarm.
         */
        private const val RECREATION_TRIGGER_RANGE_MILLIS: Long = 30L * 60L * 1000L
    }

    fun nextScoreAfterPenalty(current: Int): Int {
        return if (current > 0) -1 else current - 1
    }

    fun nextScoreAfterReward(current: Int): Int {
        return current + 1
    }

    /**
     * Dismiss was rewarded, reverse that reward and add a penalty on top.
     */
    fun nextScoreAfterDismissReversal(current: Int): Int {
        return nextScoreAfterPenalty(current - 1)
    }

    fun isDismissReversal(
        lastDismissedAlarmTime: Long,
        now: Long,
        newAlarmTriggerTime: Long,
    ): Boolean {
        val withinDetectionWindow =
            now - lastDismissedAlarmTime in 0..RECREATION_DETECTION_WINDOW_MILLIS
        val withinTriggerRange =
            newAlarmTriggerTime in lastDismissedAlarmTime..(lastDismissedAlarmTime + RECREATION_TRIGGER_RANGE_MILLIS)
        return withinDetectionWindow && withinTriggerRange
    }

    fun penaltyOffsetMillis(score: Int): Long {
        return PenaltyLevel.fromScore(score).penaltyOffsetMillis
    }

    /**
     * Shifts [time] earlier by [penaltyOffsetMillis].
     */
    fun withPenaltyOffset(time: Long, penaltyOffsetMillis: Long): Long {
        if (penaltyOffsetMillis <= 0) return time
        return time - penaltyOffsetMillis
    }
}