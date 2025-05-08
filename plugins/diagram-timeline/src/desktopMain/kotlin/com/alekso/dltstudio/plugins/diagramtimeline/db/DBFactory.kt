package com.alekso.dltstudio.plugins.diagramtimeline.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.alekso.logger.Log
import kotlinx.coroutines.Dispatchers
import java.io.File

actual class DBFactory {
    actual fun createDatabase(path: String): TimelineDatabase {
        Log.d("createDatabase $path")
        val dbFile = File(path)
        if (!dbFile.exists()) {
            dbFile.createNewFile()
        }
        return Room.databaseBuilder<TimelineDatabase>(dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration(true)
            .build()
    }
}