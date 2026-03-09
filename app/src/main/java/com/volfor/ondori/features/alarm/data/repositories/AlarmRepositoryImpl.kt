package com.volfor.ondori.features.alarm.data.repositories

import com.volfor.ondori.features.alarm.data.datasources.AlarmLocalDataSource
import com.volfor.ondori.features.alarm.data.models.toDomain
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val localDataSource: AlarmLocalDataSource,
) : AlarmRepository {

    override fun getAlarms(): List<Alarm> {
        return localDataSource.getAlarms().map {
            it.toDomain()
        }
    }
}