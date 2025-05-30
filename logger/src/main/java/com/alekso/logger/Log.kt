package com.alekso.logger

import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private var dateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS").withZone(ZoneId.systemDefault())

object Log {
    private const val MAX_FILE_SIZE = 5_000_000L

    enum class Level {
        DEBUG, INFO, WARN, ERROR
    }

    private var file: File? = null

    fun init(logFileName: String? = null) {
        if (logFileName != null) {
            file = File(logFileName)
        }
        val f = file?: return
        if (!f.parentFile.exists()) {
            f.parentFile.mkdirs()
        }
        if (!f.exists()) {
            f.createNewFile()
        }
    }

    fun d(message: String) = log(message, Level.DEBUG)
    fun i(message: String) = log(message, Level.INFO)
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
        writeLog("${getDateTime()} ${level.name} $message")
    }

    private fun getDateTime(): String {
        return dateTimeFormatter.format(Instant.ofEpochMilli(System.currentTimeMillis()))
    }

    private fun writeLog(text: String) {
        if (file == null) {
            println(text)
            // todo Rotate log file
        } else if ((file?.length() ?: 0) > MAX_FILE_SIZE) {
            file?.writeText("\r\n$text")
        } else {
            file?.appendText("\r\n$text")
        }
    }
}