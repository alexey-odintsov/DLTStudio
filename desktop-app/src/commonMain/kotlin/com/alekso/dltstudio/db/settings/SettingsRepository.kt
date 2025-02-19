package com.alekso.dltstudio.db.settings

import com.alekso.dltstudio.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun updateUISettings(item: SettingsUIEntity)
    fun getUISettingsFlow(): Flow<SettingsUIEntity>
}

class SettingsRepositoryImpl(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) : SettingsRepository {

    override suspend fun updateUISettings(item: SettingsUIEntity) {
        println("updateUISettings($item)")
        database.getSettingsDao().updateUISettings(item)
    }

    override fun getUISettingsFlow(): Flow<SettingsUIEntity> {
        return database.getSettingsDao().getUISettingsFlow()
    }
}