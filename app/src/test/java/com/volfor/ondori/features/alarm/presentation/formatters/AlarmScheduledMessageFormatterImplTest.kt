package com.volfor.ondori.features.alarm.presentation.formatters

import android.content.Context
import android.content.res.Resources
import com.volfor.ondori.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AlarmScheduledMessageFormatterImplTest {

    private lateinit var resources: Resources
    private lateinit var formatter: AlarmScheduledMessageFormatterImpl

    @Before
    fun setup() {
        resources = mockk()
        val context = mockk<Context>()
        every { context.applicationContext } returns context
        every { context.resources } returns resources
        stubDurationPlurals()
        formatter = AlarmScheduledMessageFormatterImpl(context)
    }

    @Test
    fun `returns less than one minute message when under 60 seconds`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 30_000)

        assertEquals(R.string.alarm_set_less_than_one_minute, stringRes)
        assertNull(args)
    }

    @Test
    fun `returns less than one minute message at upper boundary`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 59_999)

        assertEquals(R.string.alarm_set_less_than_one_minute, stringRes)
        assertNull(args)
    }

    @Test
    fun `returns less than one minute message for zero remaining time`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 0)

        assertEquals(R.string.alarm_set_less_than_one_minute, stringRes)
        assertNull(args)
    }

    @Test
    fun `returns duration message for one day`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 24 * 60 * 60_000L)

        assertEquals(R.string.alarm_set_for_duration, stringRes)
        assertEquals(listOf("1 day"), args)
    }

    @Test
    fun `returns duration message for two minutes`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 2 * 60_000)

        assertEquals(R.string.alarm_set_for_duration, stringRes)
        assertEquals(listOf("2 minutes"), args)
    }

    @Test
    fun `returns duration message for one hour`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 60 * 60_000)

        assertEquals(R.string.alarm_set_for_duration, stringRes)
        assertEquals(listOf("1 hour"), args)
    }

    @Test
    fun `floors partial minutes to whole minutes`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = (2 * 60_000L) + 45_000L)

        assertEquals(R.string.alarm_set_for_duration, stringRes)
        assertEquals(listOf("2 minutes"), args)
    }

    @Test
    fun `does not treat exactly one minute as less than one minute`() {
        val (stringRes, args) = formatter.format(remainingTimeMillis = 60_000)

        assertEquals(R.string.alarm_set_for_duration, stringRes)
        assertEquals(listOf("1 minute"), args)
    }

    private fun stubDurationPlurals() {
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_days, 1, 1)
        } returns "1 day"
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_hours, 1, 1)
        } returns "1 hour"
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_hours, 3, 3)
        } returns "3 hours"
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_hours, 22, 22)
        } returns "22 hours"
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_minutes, 1, 1)
        } returns "1 minute"
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_minutes, 2, 2)
        } returns "2 minutes"
        every {
            resources.getQuantityString(R.plurals.alarm_set_duration_minutes, 7, 7)
        } returns "7 minutes"
    }
}
