package com.alekso.dltstudio

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime


object TimeFormatter {
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern(DATE_TIME_FORMAT)
    }

    private val timeFormat = LocalDateTime.Format {
        byUnicodePattern(TIME_FORMAT)
    }
    var timeZone = TimeZone.currentSystemDefault()

    fun formatDateTime(timeStampNano: Long): String = format(dateTimeFormat, timeStampNano)

    fun formatTime(timeStampNano: Long): String = format(timeFormat, timeStampNano)

    private fun format(formatter: DateTimeFormat<LocalDateTime>, timeStampNano: Long): String {
        val instant =
            Instant.fromEpochSeconds(timeStampNano / 1000000L, (timeStampNano % 1000000) * 1000L)
        return instant.toLocalDateTime(timeZone).format(formatter)
    }

    internal const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS"
    private const val TIME_FORMAT = "HH:mm:ss.SSS"

}