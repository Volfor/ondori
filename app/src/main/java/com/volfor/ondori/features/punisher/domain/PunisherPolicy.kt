package com.volfor.ondori.features.punisher.domain

object PunisherPolicy {

    fun nextScoreAfterPenalty(current: Int): Int {
        return if (current > 0) -1 else current - 1
    }

    fun nextScoreAfterReward(current: Int): Int {
        return current + 1
    }

    fun penaltyOffsetMillis(score: Int): Long {
        if (score >= 0) return 0L
        val minutes = when (score) {
            -1 -> 5 // low level
            -2 -> 10 // low+ level
            in -4..-3 -> 15 // medium level
            in -6..-5 -> 20 // medium+ level
            in -8..-7 -> 30 // high level
            in -12..-9 -> 45 // critical level
            else -> 60 // extreme level
        }
        return minutes * 60L * 1000L
    }
}