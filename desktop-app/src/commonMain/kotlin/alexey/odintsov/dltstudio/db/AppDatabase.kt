package alexey.odintsov.dltstudio.db

import alexey.odintsov.dltstudio.db.preferences.ColumnParamsEntity
import alexey.odintsov.dltstudio.db.preferences.PreferencesDao
import alexey.odintsov.dltstudio.db.preferences.RecentColorFilterFileEntry
import alexey.odintsov.dltstudio.db.preferences.SearchEntity
import alexey.odintsov.dltstudio.db.settings.PluginStateEntity
import alexey.odintsov.dltstudio.db.settings.SettingsDao
import alexey.odintsov.dltstudio.db.settings.SettingsLogsEntity
import alexey.odintsov.dltstudio.db.settings.SettingsUIEntity
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

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
