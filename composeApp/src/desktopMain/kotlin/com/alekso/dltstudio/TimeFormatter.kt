package com.alekso.dltstudio

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeFormatter {
    fun formatDateTime(instant: Instant): String = dateTimeFormatter.format(instant)

    fun formatDateTime(timeStampNano: Long): String = format(dateTimeFormatter, timeStampNano)

    fun formatTime(timeStampNano: Long): String = format(timeFormatter, timeStampNano)

    private inline fun format(formatter: DateTimeFormatter, timeStampNano: Long): String {
        val instant =
            Instant.ofEpochSecond(timeStampNano / 1000000L, (timeStampNano % 1000000) * 1000L)

        return formatter.format(instant)
    }

    private const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS"
    private const val TIME_FORMAT = "HH:mm:ss.SSS"
    private var dateTimeFormatter =
        DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).withZone(ZoneId.systemDefault())
    private var timeFormatter =
        DateTimeFormatter.ofPattern(TIME_FORMAT).withZone(ZoneId.systemDefault())


}