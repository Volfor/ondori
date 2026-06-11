package com.volfor.ondori.features.alarm.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.volfor.ondori.features.alarm.data.services.RingingAlarmStoreImpl
import com.volfor.ondori.features.alarm.domain.entities.Alarm
import com.volfor.ondori.features.alarm.domain.services.RingingAlarmStore
import com.volfor.ondori.features.alarm.domain.usecases.DismissAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.GetAlarmUseCase
import com.volfor.ondori.features.alarm.domain.usecases.SnoozeAlarmUseCase
import com.volfor.ondori.features.punisher.domain.usecases.GetScoreUseCase
import com.volfor.ondori.features.settings.domain.usecases.GetSnoozeMinutesUseCase
import com.volfor.ondori.utils.Constants.EXTRA_ALARM_ID
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlarmRingingViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getAlarm: GetAlarmUseCase
    private lateinit var getSnoozeMinutes: GetSnoozeMinutesUseCase
    private lateinit var getScore: GetScoreUseCase
    private lateinit var snoozeAlarm: SnoozeAlarmUseCase
    private lateinit var dismissAlarm: DismissAlarmUseCase
    private lateinit var ringingAlarmStore: RingingAlarmStoreImpl

    private val alarm = Alarm(
        id = 1L,
        hour = 7,
        minute = 30,
        enabled = true,
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAlarm = mockk()
        getSnoozeMinutes = mockk()
        getScore = mockk()
        snoozeAlarm = mockk(relaxed = true)
        dismissAlarm = mockk(relaxed = true)
        ringingAlarmStore = RingingAlarmStoreImpl()

        coEvery { getAlarm(1L) } returns alarm
        coEvery { getScore() } returns 3
        coEvery { getSnoozeMinutes() } returns 10
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        alarmId: Long = 1L,
        store: RingingAlarmStore = ringingAlarmStore,
    ) = AlarmRingingViewModel(
        getAlarm = getAlarm,
        getSnoozeMinutes = getSnoozeMinutes,
        getScore = getScore,
        snoozeAlarm = snoozeAlarm,
        dismissAlarm = dismissAlarm,
        ringingAlarmStore = store,
        savedStateHandle = SavedStateHandle(mapOf(EXTRA_ALARM_ID to alarmId)),
    )

    @Test
    fun `loads alarm score and snooze minutes on init`() = runTest {
        val vm = viewModel()

        assertEquals(alarm, vm.uiState.alarm)
        assertEquals(3, vm.uiState.score)
        assertEquals(10, vm.uiState.snoozeMinutes)
        assertFalse(vm.uiState.isLoading)
    }

    @Test
    fun `stale load result does not overwrite current alarm`() = runTest {
        val load1Started = CompletableDeferred<Unit>()
        val load1Continue = CompletableDeferred<Unit>()
        coEvery { getAlarm(1L) } coAnswers {
            load1Started.complete(Unit)
            load1Continue.await()
            alarm
        }
        coEvery { getAlarm(2L) } returns alarm.copy(id = 2L)

        val vm = viewModel()
        load1Started.await()
        vm.onNewAlarm(2L)
        assertEquals(2L, vm.uiState.alarm?.id)

        load1Continue.complete(Unit)
        advanceUntilIdle()

        assertEquals(2L, vm.uiState.alarm?.id)
    }

    @Test
    fun `onNewAlarm updates saved state when alarm changes`() = runTest {
        coEvery { getAlarm(2L) } returns alarm.copy(id = 2L)
        val vm = viewModel()

        vm.onNewAlarm(2L)

        assertEquals(2L, vm.uiState.alarm?.id)
    }

    @Test
    fun `onNewAlarm ignores same alarm id`() = runTest {
        val vm = viewModel()
        val alarmBefore = vm.uiState.alarm

        vm.onNewAlarm(1L)

        assertEquals(alarmBefore, vm.uiState.alarm)
    }

    @Test
    fun `ringing alarm store switch loads new alarm`() = runTest {
        coEvery { getAlarm(2L) } returns alarm.copy(id = 2L)
        viewModel()

        ringingAlarmStore.setRingingAlarm(2L)

        coVerify { getAlarm(2L) }
    }

    @Test
    fun `stopped alarm matching current alarm marks handled`() = runTest {
        val vm = viewModel()
        ringingAlarmStore.setRingingAlarm(1L)

        ringingAlarmStore.clear()

        assertTrue(vm.uiState.isAlarmHandled)
    }

    @Test
    fun `stopped alarm for different alarm does not mark handled`() = runTest {
        val ringingId = MutableStateFlow<Long?>(1L)
        val stopped = MutableSharedFlow<Long>(replay = 1)
        val store = mockk<RingingAlarmStore> {
            every { ringingAlarmId } returns ringingId
            every { stoppedAlarmId } returns stopped
            every { setRingingAlarm(any()) } answers { ringingId.value = firstArg() }
            every { clear() } just runs
        }
        val vm = viewModel(store = store)

        stopped.emit(2L)

        assertFalse(vm.uiState.isAlarmHandled)
    }

    @Test
    fun `replayed stop from previous ring does not mark new ring handled`() = runTest {
        ringingAlarmStore.setRingingAlarm(1L)
        ringingAlarmStore.clear()
        ringingAlarmStore.setRingingAlarm(1L)

        val vm = viewModel()

        assertFalse(vm.uiState.isAlarmHandled)
    }

    @Test
    fun `snooze marks alarm handled`() = runTest {
        coEvery { snoozeAlarm(1L) } just runs
        val vm = viewModel()

        vm.snooze()

        coVerify { snoozeAlarm(1L) }
        assertTrue(vm.uiState.isAlarmHandled)
    }

    @Test
    fun `dismiss marks alarm handled`() = runTest {
        coEvery { dismissAlarm(1L) } just runs
        val vm = viewModel()

        vm.dismiss()

        coVerify { dismissAlarm(1L) }
        assertTrue(vm.uiState.isAlarmHandled)
    }
}
