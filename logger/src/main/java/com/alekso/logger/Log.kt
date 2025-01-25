package com.alekso.logger

import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private var dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS").withZone(ZoneId.systemDefault())

object Log {
    private const val MAX_FILE_SIZE = 10_000_000L
    private const val DEFAULT_LOG_FILE_NAME = "dltstudio.log"

    enum class Level {
        DEBUG, INFO, WARN, ERROR
    }

    private var path: String = "${System.getProperty("user.home")}/$DEFAULT_LOG_FILE_NAME"

    fun init(logFileName: String? = null) {
        if (logFileName != null) {
            path = logFileName
        }
    }


    private val file = File(path)


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
        // todo Rotate log file
        if (file.length() > MAX_FILE_SIZE) {
            file.writeText("\r\n$text")
        } else {
            file.appendText("\r\n$text")
        }

    }
}