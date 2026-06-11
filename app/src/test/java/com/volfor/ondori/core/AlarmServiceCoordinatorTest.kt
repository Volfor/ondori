package com.volfor.ondori.core

import android.app.Notification
import com.volfor.ondori.app.notifications.AlarmNotificationBuilder
import com.volfor.ondori.features.alarm.data.services.RingingAlarmStoreImpl
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.usecases.ApplyPenaltyAndRescheduleEnabledAlarmsUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.MissAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.GetScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.GetIncreasingVolumeEnabledUseCase
import com.volfor.ondori.utils.Constants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmServiceCoordinatorTest {

    private companion object {
        const val ALARM_ID = 5L
        const val OTHER_ALARM_ID = 7L
        val MISSED_TIMEOUT_MILLIS = Constants.Alarm.MISSED_TIMEOUT_MILLIS
    }

    private lateinit var getAlarm: GetAlarmUseCase
    private lateinit var getScore: GetScoreUseCase
    private lateinit var missAlarm: MissAlarmUseCase
    private lateinit var applyPenaltyAndRescheduleEnabledAlarms: ApplyPenaltyAndRescheduleEnabledAlarmsUseCase
    private lateinit var getIncreasingVolumeEnabled: GetIncreasingVolumeEnabledUseCase
    private lateinit var store: RingingAlarmStoreImpl
    private lateinit var alarmSoundPlayer: AlarmSoundPlayer
    private lateinit var alarmVibrator: AlarmVibrator
    private lateinit var notificationBuilder: AlarmNotificationBuilder
    private lateinit var host: AlarmServiceHost

    private val alarm = Alarm(id = ALARM_ID, hour = 7, minute = 30, enabled = true)
    private val notification = mockk<Notification>()

    @Before
    fun setup() {
        getAlarm = mockk(relaxed = true)
        getScore = mockk(relaxed = true)
        missAlarm = mockk(relaxed = true)
        applyPenaltyAndRescheduleEnabledAlarms = mockk(relaxed = true)
        getIncreasingVolumeEnabled = mockk(relaxed = true)
        store = RingingAlarmStoreImpl()
        alarmSoundPlayer = mockk(relaxed = true)
        alarmVibrator = mockk(relaxed = true)
        notificationBuilder = mockk(relaxed = true)
        host = mockk(relaxed = true)

        coEvery { getAlarm(ALARM_ID) } returns alarm
        coEvery { getScore() } returns 3
        coEvery { getIncreasingVolumeEnabled() } returns false
        coEvery { notificationBuilder.build(alarm, 3) } returns notification
    }

    private fun TestScope.coordinator() = AlarmServiceCoordinator(
        getAlarm = getAlarm,
        getScore = getScore,
        missAlarm = missAlarm,
        applyPenaltyAndRescheduleEnabledAlarms = applyPenaltyAndRescheduleEnabledAlarms,
        getIncreasingVolumeEnabled = getIncreasingVolumeEnabled,
        ringingAlarmStore = store,
        alarmSoundPlayer = alarmSoundPlayer,
        alarmVibrator = alarmVibrator,
        notificationBuilder = notificationBuilder,
        applicationScope = this,
    )

    /**
     * Launches the observe loop as a foreground job so the virtual-time helpers process its
     * continuations, then runs [block] and cancels the never-completing loop afterwards.
     */
    private fun TestScope.observing(
        coordinator: AlarmServiceCoordinator,
        block: TestScope.() -> Unit,
    ) {
        val job = launch { coordinator.observeRingingAlarm(host) }
        runCurrent()
        try {
            block()
        } finally {
            job.cancel()
        }
    }

    // region handleStartCommand

    @Test
    fun `start without alarm id stops service`() = runTest {
        coordinator().handleStartCommand(null, AlarmServiceCoordinator.NO_ALARM_ID, host)

        verify { host.stop() }
        assertNull(store.ringingAlarmId.value)
    }

    @Test
    fun `start sets ringing alarm`() = runTest {
        coordinator().handleStartCommand(null, ALARM_ID, host)
        advanceUntilIdle()

        assertEquals(ALARM_ID, store.ringingAlarmId.value)
        coVerify(exactly = 0) { missAlarm(any()) }
    }

    @Test
    fun `start for new alarm while another rings misses previous one`() = runTest {
        store.setRingingAlarm(OTHER_ALARM_ID)

        coordinator().handleStartCommand(null, ALARM_ID, host)
        advanceUntilIdle()

        assertEquals(ALARM_ID, store.ringingAlarmId.value)
        coVerify(exactly = 1) { missAlarm(OTHER_ALARM_ID) }
    }

    @Test
    fun `start for already ringing alarm does not miss it`() = runTest {
        store.setRingingAlarm(ALARM_ID)

        coordinator().handleStartCommand(null, ALARM_ID, host)
        advanceUntilIdle()

        assertEquals(ALARM_ID, store.ringingAlarmId.value)
        coVerify(exactly = 0) { missAlarm(any()) }
    }

    @Test
    fun `stop action for ringing alarm clears store`() = runTest {
        store.setRingingAlarm(ALARM_ID)

        coordinator().handleStartCommand(AlarmService.ACTION_STOP_RINGING_FOR_ALARM, ALARM_ID, host)

        assertNull(store.ringingAlarmId.value)
        verify(exactly = 0) { host.stop() }
    }

    @Test
    fun `stop action for different alarm keeps current one ringing`() = runTest {
        store.setRingingAlarm(ALARM_ID)

        coordinator().handleStartCommand(
            AlarmService.ACTION_STOP_RINGING_FOR_ALARM,
            OTHER_ALARM_ID,
            host,
        )

        assertEquals(ALARM_ID, store.ringingAlarmId.value)
        verify(exactly = 0) { host.stop() }
    }

    @Test
    fun `stop action when nothing rings stops service`() = runTest {
        coordinator().handleStartCommand(AlarmService.ACTION_STOP_RINGING_FOR_ALARM, ALARM_ID, host)

        verify { host.stop() }
    }

    // endregion

    // region observeRingingAlarm

    @Test
    fun `rings alarm in foreground when ringing alarm appears`() = runTest {
        coEvery { getIncreasingVolumeEnabled() } returns true

        val coordinator = coordinator()
        observing(coordinator) {
            coordinator.handleStartCommand(null, ALARM_ID, host)
            runCurrent()

            verify { host.startInForeground(notification) }
            verify { alarmVibrator.vibrate() }
            verify { alarmSoundPlayer.play(alarm.sound, gradualVolumeIncrease = true) }
            verify(exactly = 0) { host.stop() }
        }
    }

    @Test
    fun `plays sound without volume ramp when setting disabled`() = runTest {
        val coordinator = coordinator()
        observing(coordinator) {
            coordinator.handleStartCommand(null, ALARM_ID, host)
            runCurrent()

            verify { alarmSoundPlayer.play(alarm.sound, gradualVolumeIncrease = false) }
        }
    }

    @Test
    fun `clears store and stops when ringing alarm does not exist`() = runTest {
        coEvery { getAlarm(ALARM_ID) } returns null

        val coordinator = coordinator()
        observing(coordinator) {
            coordinator.handleStartCommand(null, ALARM_ID, host)
            advanceUntilIdle()

            assertNull(store.ringingAlarmId.value)
            verify(exactly = 0) { host.startInForeground(any()) }
            verify { host.stop() }
        }
    }

    @Test
    fun `misses alarm and applies penalty when ringing times out`() = runTest {
        val coordinator = coordinator()
        observing(coordinator) {
            coordinator.handleStartCommand(null, ALARM_ID, host)
            advanceTimeBy((MISSED_TIMEOUT_MILLIS + 100).milliseconds)
            advanceUntilIdle()

            coVerify(exactly = 1) { missAlarm(ALARM_ID) }
            coVerify(exactly = 1) { applyPenaltyAndRescheduleEnabledAlarms() }
        }
    }

    @Test
    fun `does not miss alarm stopped before timeout and stops service`() = runTest {
        val coordinator = coordinator()
        observing(coordinator) {
            coordinator.handleStartCommand(null, ALARM_ID, host)
            advanceTimeBy((MISSED_TIMEOUT_MILLIS - 100).milliseconds)
            coordinator.handleStartCommand(
                AlarmService.ACTION_STOP_RINGING_FOR_ALARM, ALARM_ID, host
            )
            advanceUntilIdle()

            coVerify(exactly = 0) { missAlarm(any()) }
            coVerify(exactly = 0) { applyPenaltyAndRescheduleEnabledAlarms() }
            verify { host.stop() }
        }
    }

    @Test
    fun `does not stop service when no alarm ever rang`() = runTest {
        val coordinator = coordinator()
        observing(coordinator) {
            advanceUntilIdle()

            verify(exactly = 0) { host.stop() }
        }
    }

    @Test
    fun `new ringing alarm cancels timeout of previous one`() = runTest {
        val otherAlarm = alarm.copy(id = OTHER_ALARM_ID)
        coEvery { getAlarm(OTHER_ALARM_ID) } returns otherAlarm
        coEvery { notificationBuilder.build(otherAlarm, 3) } returns notification

        val coordinator = coordinator()
        observing(coordinator) {
            store.setRingingAlarm(ALARM_ID)
            advanceTimeBy((MISSED_TIMEOUT_MILLIS / 2).milliseconds)
            store.setRingingAlarm(OTHER_ALARM_ID)
            advanceTimeBy((MISSED_TIMEOUT_MILLIS + 100).milliseconds)
            advanceUntilIdle()

            coVerify(exactly = 0) { missAlarm(ALARM_ID) }
            coVerify(exactly = 1) { missAlarm(OTHER_ALARM_ID) }
        }
    }

    // endregion

    // region release

    @Test
    fun `release clears store and stops sound and vibration`() = runTest {
        val coordinator = coordinator()
        coordinator.handleStartCommand(null, ALARM_ID, host)

        coordinator.release()

        assertNull(store.ringingAlarmId.value)
        verify { alarmVibrator.stop() }
        verify { alarmSoundPlayer.stop() }
    }

    // endregion
}
