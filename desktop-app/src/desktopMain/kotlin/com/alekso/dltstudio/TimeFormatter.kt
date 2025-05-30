package com.alekso.dltstudio

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alekso.dltstudio.model.contract.Formatter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

private const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS"
private const val TIME_FORMAT = "HH:mm:ss.SSS"

class AppFormatter : Formatter {
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(DATE_TIME_FORMAT)
    }

    private val timeFormat = LocalDateTime.Format {
        byUnicodePattern(TIME_FORMAT)
    }
    private var _timeZone by mutableStateOf(TimeZone.currentSystemDefault())

    override fun formatDateTime(timeStampUs: Long): String = format(dateTimeFormat, timeStampUs)

    override fun formatTime(timeStampUs: Long): String = format(timeFormat, timeStampUs)

    override fun formatSizeHuman(size: Long): String {
        return when {
            size < 1024 -> "$size b\u00A0"
            size < 1024 * 1024 -> "%.0f Kb".format(size / 1024f)
            else -> "%.0f Mb".format(size / (1024 * 1024f))
        }
    }

    override fun setTimeZone(timeZone: TimeZone) {
        _timeZone = timeZone
    }

    override fun getTimeZone(): TimeZone = _timeZone

    private fun format(formatter: DateTimeFormat<LocalDateTime>, timeStampUs: Long): String {
        val instant =
            Instant.fromEpochSeconds(timeStampUs / 1_000_000L, (timeStampUs % 1_000_000L) * 1000L)
        return instant.toLocalDateTime(_timeZone).format(formatter)
    }

}