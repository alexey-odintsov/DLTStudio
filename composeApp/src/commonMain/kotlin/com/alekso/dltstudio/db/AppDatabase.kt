package com.alekso.dltstudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceDao
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceEntity

@Database(entities = [VirtualDeviceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase(), DB {

    abstract fun getVirtualDeviceDao(): VirtualDeviceDao

    override fun clearAllTables() {
        super.clearAllTables()
    }
}


internal const val dbFileName = "app_room_db2.db"


interface DB {
    fun clearAllTables() {}
}