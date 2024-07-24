package com.alekso.dltstudio.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceDao
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceEntity
import kotlinx.coroutines.Dispatchers
import java.io.File
//
//@Database(entities = [VirtualDeviceEntity::class], version = 1)
//abstract class AppDatabase: RoomDatabase() {
//    abstract fun getVirtualDeviceDao(): VirtualDeviceDao
//}
//
//
//fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
//    val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
//    return Room.databaseBuilder<AppDatabase>(
//        name = dbFile.absolutePath,
//    )
//}
//
//fun getRoomDatabase(
//    builder: RoomDatabase.Builder<AppDatabase>
//): AppDatabase {
//    return builder
//        //.addMigrations(MIGRATIONS)
////        .fallbackToDestructiveMigrationOnDowngrade()
//        .setDriver(BundledSQLiteDriver())
//        .setQueryCoroutineContext(Dispatchers.IO)
//        .build()
//}
