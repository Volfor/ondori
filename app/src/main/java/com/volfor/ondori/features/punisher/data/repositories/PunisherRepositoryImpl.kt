package com.volfor.ondori.features.punisher.data.repositories

import androidx.room.withTransaction
import com.volfor.ondori.data.local.db.OndoriDatabase
import com.volfor.ondori.features.punisher.data.local.db.dao.UserProfileDao
import com.volfor.ondori.features.punisher.domain.PunisherPolicy
import com.volfor.ondori.features.punisher.domain.repositories.PunisherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PunisherRepositoryImpl @Inject constructor(
    private val db: OndoriDatabase,
    private val dao: UserProfileDao,
    private val punisher: PunisherPolicy,
) : PunisherRepository {

    override fun observeScore(): Flow<Int> {
        return dao.observeScore()
    }

    override suspend fun getScore(): Int {
        return dao.getScore()
    }

    override suspend fun applyPenalty() = db.withTransaction {
        dao.updateScore(punisher.nextScoreAfterPenalty(dao.getScore()))
    }

    override suspend fun applyReward() = db.withTransaction {
        dao.updateScore(punisher.nextScoreAfterReward(dao.getScore()))
    }

    override suspend fun applyDismissReversalPenalty() = db.withTransaction {
        dao.updateScore(punisher.nextScoreAfterDismissReversal(dao.getScore()))
    }

    override suspend fun getLastDismissedAlarmTime(): Long? {
        return dao.getLastDismissedAlarmTime()
    }

    override suspend fun setLastDismissedAlarmTime(time: Long?) {
        dao.updateLastDismissedAlarmTime(time)
    }
}