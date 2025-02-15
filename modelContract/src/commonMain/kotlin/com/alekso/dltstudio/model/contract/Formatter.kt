package com.alekso.dltstudio.model.contract

import kotlinx.datetime.TimeZone

interface Formatter {
    fun formatDateTime(timeStampNano: Long): String
    fun formatTime(timeStampNano: Long): String
    fun formatSizeHuman(size: Long): String
    fun setTimeZone(timeZone: TimeZone)
    fun getTimeZone(): TimeZone

    companion object {
        val STUB = object : Formatter {
            override fun formatDateTime(timeStampNano: Long): String = ""
            override fun formatTime(timeStampNano: Long): String = ""
            override fun formatSizeHuman(size: Long): String = ""
            override fun setTimeZone(timeZone: TimeZone) = Unit
            override fun getTimeZone(): TimeZone = TimeZone.currentSystemDefault()
        }
    }
}