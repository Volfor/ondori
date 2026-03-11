package com.volfor.ondori.features.alarm.domain.usecases

import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.repositories.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmsStreamUseCase @Inject constructor(
    private val alarmRepository: AlarmRepository
) {
    operator fun invoke(): Flow<List<Alarm>> {
        return alarmRepository.getAlarmsStream()
    }
}