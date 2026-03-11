package com.volfor.ondori.features.alarm.data.repositories

import com.volfor.ondori.features.alarm.data.local.db.dao.AlarmDao
import com.volfor.ondori.features.alarm.data.mappers.toDomain
import com.volfor.ondori.features.alarm.data.mappers.toLocal
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val localDataSource: AlarmDao,
) : AlarmRepository {

    override fun getAlarmsStream(): Flow<List<Alarm>> {
        return localDataSource.observeAll().map { alarms ->
            alarms.toDomain()
        }
    }

    override suspend fun createAlarm(alarm: Alarm): Long {
        return localDataSource.insert(alarm.toLocal())
    }
}