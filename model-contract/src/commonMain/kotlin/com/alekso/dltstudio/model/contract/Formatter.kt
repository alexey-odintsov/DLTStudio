package com.alekso.dltstudio.model.contract

import kotlinx.datetime.TimeZone

interface Formatter {
    fun formatDateTime(timeStampUs: Long): String
    fun formatTime(timeStampUs: Long): String
    fun formatSizeHuman(size: Long): String
    fun setTimeZone(timeZone: TimeZone)
    fun getTimeZone(): TimeZone

    companion object {
        val STUB = object : Formatter {
            override fun formatDateTime(timeStampUs: Long): String = ""
            override fun formatTime(timeStampUs: Long): String = ""
            override fun formatSizeHuman(size: Long): String = ""
            override fun setTimeZone(timeZone: TimeZone) = Unit
            override fun getTimeZone(): TimeZone = TimeZone.currentSystemDefault()
        }
    }
}