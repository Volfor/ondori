package com.volfor.ondori.features.punisher.domain.entities

sealed class PenaltyLevel(
    val penaltyOffsetMillis: Long
) {
    data object Zero : PenaltyLevel(0)
    data object Low : PenaltyLevel(minutesToMillis(5))
    data object ModerateLow : PenaltyLevel(minutesToMillis(10))
    data object Medium : PenaltyLevel(minutesToMillis(15))
    data object ModerateHigh : PenaltyLevel(minutesToMillis(20))
    data object High : PenaltyLevel(minutesToMillis(30))
    data object Critical : PenaltyLevel(minutesToMillis(45))
    data object Extreme : PenaltyLevel(minutesToMillis(60))

    companion object {
        fun fromScore(score: Int): PenaltyLevel = when {
            score >= 0 -> Zero
            score == -1 -> Low
            score == -2 -> ModerateLow
            score in -4..-3 -> Medium
            score in -6..-5 -> ModerateHigh
            score in -8..-7 -> High
            score in -12..-9 -> Critical
            else -> Extreme
        }

        private fun minutesToMillis(minutes: Int): Long {
            return minutes * 60L * 1000L
        }
    }
}

