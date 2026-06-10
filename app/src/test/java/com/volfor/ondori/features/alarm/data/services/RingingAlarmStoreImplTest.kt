package com.volfor.ondori.features.alarm.data.services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class RingingAlarmStoreImplTest {

    private lateinit var store: RingingAlarmStoreImpl

    @Before
    fun setup() {
        store = RingingAlarmStoreImpl()
    }

    @Test
    fun `ringing alarm id is null initially`() {
        assertNull(store.ringingAlarmId.value)
    }

    @Test
    fun `setRingingAlarm updates ringing alarm id`() {
        store.setRingingAlarm(42L)

        assertEquals(42L, store.ringingAlarmId.value)
    }

    @Test
    fun `clear resets ringing alarm id and emits stopped alarm id`() = runBlocking {
        store.setRingingAlarm(7L)

        store.clear()

        assertNull(store.ringingAlarmId.value)
        assertEquals(7L, store.stoppedAlarmId.first())
    }

    @Test
    fun `clear when not ringing does nothing`() = runBlocking {
        store.clear()

        assertNull(store.ringingAlarmId.value)
    }

    @Test
    fun `stopped alarm id replays last value for late collectors`() = runBlocking {
        store.setRingingAlarm(3L)
        store.clear()

        assertEquals(3L, store.stoppedAlarmId.first())
    }
}
