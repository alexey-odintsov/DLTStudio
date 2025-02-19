package com.alekso.dltstudio.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.alekso.dltstudio.Env
import kotlinx.coroutines.Dispatchers
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DBFactory {
    actual fun createDatabase(): AppDatabase {
        val dbFile = File(Env.getDbPath())
        return Room.databaseBuilder<AppDatabase>(dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
//            .fallbackToDestructiveMigration(true)
            .build()
    }
}