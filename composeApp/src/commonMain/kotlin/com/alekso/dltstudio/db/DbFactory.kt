package com.alekso.dltstudio.db

expect class DBFactory {
    fun createDatabase(): AppDatabase
}