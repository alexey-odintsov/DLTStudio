package com.alekso.logger

import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private var dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS").withZone(ZoneId.systemDefault())

object Log {
    private val path: String = "${System.getProperty("user.home")}/dltstudio.log"
    private val file = File(path)

    enum class Level {
        DEBUG, INFO, WARN, ERROR
    }

    fun d(message: String) = log(message, Level.DEBUG)
    fun w(message: String) = log(message, Level.WARN)
    fun e(message: String) = log(message, Level.ERROR)

    /**
     * Writes raw message to file
     */
    fun r(message: String) {
        writeLog(message)
    }

    /**
     * Writes log message to file
     */
    fun log(
        message: String,
        level: Level = Level.DEBUG
    ) {
        writeLog("${dateTimeFormatter.format(Instant.ofEpochMilli(System.currentTimeMillis()))} ${level.name} $message")
    }

    private fun writeLog(text: String) {
        if (file.length() > 10_000_000L) {
            file.writeText("\r\n$text")
        } else {
            file.appendText("\r\n$text")
        }

    }
}