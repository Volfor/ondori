package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAlarmsUseCase @Inject constructor(
    private val repo: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return repo.observeAlarms()
    }
}