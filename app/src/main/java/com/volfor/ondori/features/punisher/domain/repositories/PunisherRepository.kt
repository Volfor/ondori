package com.volfor.ondori.features.punisher.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PunisherRepository {

    fun observeScore(): Flow<Int>

    suspend fun getScore(): Int

    suspend fun applyPenalty()

    suspend fun applyReward()

    suspend fun applyDismissReversalPenalty()

    suspend fun getLastDismissedAlarmTime(): Long?

    suspend fun setLastDismissedAlarmTime(time: Long?)
}