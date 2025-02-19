package com.alekso.dltstudio.db.settings

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Use for insert and update
    suspend fun updateUISettings(item: SettingsUIEntity)

    @Query("SELECT * FROM SettingsUIEntity LIMIT 1")
    fun getUISettingsFlow(): Flow<SettingsUIEntity>

}