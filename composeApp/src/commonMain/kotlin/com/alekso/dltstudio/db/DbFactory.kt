package com.alekso.dltstudio.db

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DBFactory {
    fun createDatabase(): AppDatabase
}