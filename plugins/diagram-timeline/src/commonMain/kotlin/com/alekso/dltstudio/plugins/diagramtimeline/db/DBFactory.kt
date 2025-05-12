package com.alekso.dltstudio.plugins.diagramtimeline.db

expect class DBFactory {
    fun createDatabase(path: String): TimelineDatabase
}