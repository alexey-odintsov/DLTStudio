package com.alekso.dltstudio.plugins.virtualdevice

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alekso.dltstudio.plugins.virtualdevice.db.VirtualDeviceDao
import com.alekso.dltstudio.plugins.virtualdevice.db.VirtualDeviceEntity

@Database(
    entities = [
        VirtualDeviceEntity::class,
    ],
    version = 3, exportSchema = true,
)
abstract class VirtualDeviceDatabase : RoomDatabase() {
    abstract fun getVirtualDeviceDao(): VirtualDeviceDao
}