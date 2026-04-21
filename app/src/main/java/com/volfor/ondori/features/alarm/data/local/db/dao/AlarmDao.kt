package com.volfor.ondori.features.alarm.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.volfor.ondori.features.alarm.data.local.db.entities.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    /**
     * Observes list of alarms.
     *
     * @return all alarms.
     */
    @Query("SELECT * FROM alarm")
    fun observeAll(): Flow<List<AlarmEntity>>

    /**
     * Observes a single alarm.
     *
     * @param alarmId the alarm id.
     * @return the alarm with alarmId.
     */
    @Query("SELECT * FROM alarm WHERE id = :alarmId")
    fun observeById(alarmId: Long): Flow<AlarmEntity>

    /**
     * Select all alarms from the alarms table.
     *
     * @return all alarms.
     */
    @Query("SELECT * FROM alarm")
    suspend fun getAll(): List<AlarmEntity>

    /**
     * Select all enabled alarms from the alarms table.
     *
     * @return all enabled alarms.
     */
    @Query("SELECT * FROM alarm WHERE enabled = 1")
    suspend fun getAllEnabled(): List<AlarmEntity>

    /**
     * Select an alarm by id.
     *
     * @param alarmId the alarm id.
     * @return the alarm with alarmId.
     */
    @Query("SELECT * FROM alarm WHERE id = :alarmId")
    suspend fun getById(alarmId: Long): AlarmEntity?

    /**
     * Insert an alarm in the database.
     *
     * @param alarm the alarm to be inserted or updated.
     * @return the id of inserted alarm.
     */
    @Insert
    suspend fun insert(alarm: AlarmEntity): Long

    /**
     * Insert or update an alarm in the database. If an alarm already exists, replace it.
     *
     * @param alarm the alarm to be inserted or updated.
     */
    @Upsert
    suspend fun upsert(alarm: AlarmEntity)

    /**
     * Insert or update alarms in the database. If an alarm already exists, replace it.
     *
     * @param alarms the alarms to be inserted or updated.
     */
    @Upsert
    suspend fun upsertAll(alarms: List<AlarmEntity>)

    /**
     * Update the enabled status of an alarm
     *
     * @param alarmId id of the alarm
     * @param enabled status to be updated
     */
    @Query("UPDATE alarm SET enabled = :enabled WHERE id = :alarmId")
    suspend fun updateEnabled(alarmId: Long, enabled: Boolean)

    /**
     * Delete an alarm by id.
     *
     * @return the number of alarms deleted. This should always be 1.
     */
    @Query("DELETE FROM alarm WHERE id = :alarmId")
    suspend fun deleteById(alarmId: Long): Int

    /**
     * Delete all alarms.
     */
    @Query("DELETE FROM alarm")
    suspend fun deleteAll()
}