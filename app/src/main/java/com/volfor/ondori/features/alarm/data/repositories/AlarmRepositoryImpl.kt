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

    override fun observeAlarms(): Flow<List<Alarm>> {
        return localDataSource.observeAll().map { alarms ->
            alarms.toDomain()
        }
    }

    override suspend fun getEnabledAlarms(): List<Alarm> {
        return localDataSource.getAllEnabled().toDomain()
    }

    override suspend fun getAlarm(alarmId: Long): Alarm? {
        return localDataSource.getById(alarmId)?.toDomain()
    }

    override suspend fun createAlarm(alarm: Alarm): Long {
        return localDataSource.insert(alarm.toLocal())
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        localDataSource.upsert(alarm.toLocal())
    }

    override suspend fun deleteAlarm(alarmId: Long) {
        localDataSource.deleteById(alarmId)
    }

    override suspend fun enableAlarm(alarmId: Long) {
        localDataSource.updateEnabled(alarmId, true)
    }

    override suspend fun disableAlarm(alarmId: Long) {
        localDataSource.updateEnabled(alarmId, false)
    }
}