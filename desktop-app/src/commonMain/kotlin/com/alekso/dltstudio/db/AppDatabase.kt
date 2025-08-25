package com.alekso.dltstudio.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.alekso.dltstudio.db.preferences.ColumnParamsEntity
import com.alekso.dltstudio.db.preferences.PreferencesDao
import com.alekso.dltstudio.db.preferences.RecentColorFilterFileEntry
import com.alekso.dltstudio.db.preferences.SearchEntity
import com.alekso.dltstudio.db.settings.PluginStateEntity
import com.alekso.dltstudio.db.settings.SettingsDao
import com.alekso.dltstudio.db.settings.SettingsLogsEntity
import com.alekso.dltstudio.db.settings.SettingsUIEntity

@Database(
    entities = [
        SettingsUIEntity::class,
        SettingsLogsEntity::class,
        PluginStateEntity::class,
        SearchEntity::class,
        RecentColorFilterFileEntry::class,
        ColumnParamsEntity::class,
    ],
    version = 10, exportSchema = true,
    autoMigrations = [AutoMigration(from = 9, to = 10)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getSettingsDao(): SettingsDao
    abstract fun getPreferencesDao(): PreferencesDao
}
