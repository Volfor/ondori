package com.volfor.ondori.features.alarm.presentation.formatters

import android.content.Context
import android.content.res.Resources
import android.icu.text.ListFormatter
import com.volfor.ondori.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface AlarmScheduledMessageFormatter {
    fun format(timeUntilAlarmMillis: Long): Pair<Int, List<Any>?>
}

class AlarmScheduledMessageFormatterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AlarmScheduledMessageFormatter {

    private val resources: Resources = context.applicationContext.resources

    override fun format(timeUntilAlarmMillis: Long): Pair<Int, List<Any>?> {
        if (isLessThanOneMinute(timeUntilAlarmMillis)) {
            return R.string.alarm_set_less_than_one_minute to null
        }

        val labels = durationLabels(timeUntilAlarmMillis)
        val duration = joinDurationLabels(labels.ifEmpty {
            listOf(
                resources.getQuantityString(R.plurals.alarm_set_duration_minutes, 1, 1),
            )
        })

        return R.string.alarm_set_for_duration to listOf(duration)
    }

    private fun isLessThanOneMinute(timeUntilAlarmMillis: Long): Boolean =
        timeUntilAlarmMillis < 60_000

    private fun durationLabels(timeUntilAlarmMillis: Long): List<String> {
        val minutesUntilAlarm = (timeUntilAlarmMillis + 59_999) / 60_000
        val days = (minutesUntilAlarm / (24 * 60)).toInt()
        val hours = ((minutesUntilAlarm % (24 * 60)) / 60).toInt()
        val minutes = (minutesUntilAlarm % 60).toInt()

        return buildList {
            if (days > 0) add(
                resources.getQuantityString(
                    R.plurals.alarm_set_duration_days, days, days
                )
            )
            if (hours > 0) add(
                resources.getQuantityString(
                    R.plurals.alarm_set_duration_hours, hours, hours
                )
            )
            if (minutes > 0) add(
                resources.getQuantityString(
                    R.plurals.alarm_set_duration_minutes, minutes, minutes
                )
            )
        }
    }

    private fun joinDurationLabels(labels: List<String>): String {
        require(labels.isNotEmpty())
        if (labels.size == 1) return labels[0]
        return ListFormatter.getInstance().format(*labels.toTypedArray())
    }
}
