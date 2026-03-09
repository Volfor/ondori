package com.volfor.ondori.features.alarm.data.datasources

import com.volfor.ondori.features.alarm.data.models.AlarmModel
import javax.inject.Inject

interface AlarmLocalDataSource {
    fun getAlarms(): List<AlarmModel>
}

class AlarmLocalDataSourceImpl @Inject constructor() : AlarmLocalDataSource {

    override fun getAlarms(): List<AlarmModel> {
        TODO("Not yet implemented")
    }
}

class AlarmFakeLocalDataSourceImpl @Inject constructor() : AlarmLocalDataSource {
    override fun getAlarms(): List<AlarmModel> {
        return listOf(
            AlarmModel(
                id = 0,
                label = "Test 0",
                enabled = false,
            ),
            AlarmModel(
                id = 1,
                label = "Test 1",
                enabled = true,
            ),
            AlarmModel(
                id = 2,
                label = "Test 2",
                enabled = true,
            ),
            AlarmModel(
                id = 3,
                label = "Test 3",
                enabled = true,
            ),
            AlarmModel(
                id = 4,
                label = "Test 4",
                enabled = false,
            ),
            AlarmModel(
                id = 5,
                label = "Test 5",
                enabled = false,
            ),
        )
    }
}