package com.alekso.dltstudio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alekso.dltstudio.db.preferences.ColumnParamsEntity
import com.alekso.dltstudio.db.preferences.PreferencesDao
import com.alekso.dltstudio.db.preferences.RecentColorFilterFileEntry
import com.alekso.dltstudio.db.preferences.RecentTimelineFilterFileEntry
import com.alekso.dltstudio.db.preferences.SearchEntity
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
        SearchEntity::class,
        RecentColorFilterFileEntry::class,
        RecentTimelineFilterFileEntry::class,
        ColumnParamsEntity::class,
    ],
    version = 6, exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getVirtualDeviceDao(): VirtualDeviceDao
    abstract fun getSettingsDao(): SettingsDao
    abstract fun getPreferencesDao(): PreferencesDao
}
