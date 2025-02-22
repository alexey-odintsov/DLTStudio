package com.alekso.dltstudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alekso.dltstudio.db.settings.SettingsDao
import com.alekso.dltstudio.db.settings.SettingsLogsEntity
import com.alekso.dltstudio.db.settings.SettingsUIEntity
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceDao
import com.alekso.dltstudio.db.virtualdevice.VirtualDeviceEntity

@Database(
    entities = [
        VirtualDeviceEntity::class,
        SettingsUIEntity::class,
        SettingsLogsEntity::class,
    ], version = 3, exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getVirtualDeviceDao(): VirtualDeviceDao
    abstract fun getSettingsDao(): SettingsDao
}
