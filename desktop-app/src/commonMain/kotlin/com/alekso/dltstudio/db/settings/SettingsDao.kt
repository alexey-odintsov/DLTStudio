package com.alekso.dltstudio.db.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Use for insert and update
    suspend fun updateSettingsUI(item: SettingsUIEntity)

    @Query("SELECT * FROM SettingsUIEntity LIMIT 1")
    fun getSettingsUIFlow(): Flow<SettingsUIEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettingsLogs(item: SettingsLogsEntity)

    @Query("SELECT * FROM SettingsLogsEntity LIMIT 1")
    fun getSettingsLogsFlow(): Flow<SettingsLogsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettingsPlugins(item: PluginStateEntity)

    @Query("SELECT * FROM PluginStateEntity")
    fun getPluginsStatesFlow(): Flow<List<PluginStateEntity>>

}