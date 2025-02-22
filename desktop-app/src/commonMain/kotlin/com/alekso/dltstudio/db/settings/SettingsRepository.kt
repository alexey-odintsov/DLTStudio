package com.alekso.dltstudio.db.settings

import com.alekso.dltstudio.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun updateSettingsUI(item: SettingsUIEntity)
    fun getSettingsUIFlow(): Flow<SettingsUIEntity>
    suspend fun updateSettingsLogs(item: SettingsLogsEntity)
    fun getSettingsLogsFlow(): Flow<SettingsLogsEntity>
}

class SettingsRepositoryImpl(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) : SettingsRepository {

    override suspend fun updateSettingsUI(item: SettingsUIEntity) {
        database.getSettingsDao().updateSettingsUI(item)
    }

    override fun getSettingsUIFlow(): Flow<SettingsUIEntity> {
        return database.getSettingsDao().getSettingsUIFlow()
    }

    override suspend fun updateSettingsLogs(item: SettingsLogsEntity) {
        database.getSettingsDao().updateSettingsLogs(item)
    }

    override fun getSettingsLogsFlow(): Flow<SettingsLogsEntity> {
        return database.getSettingsDao().getSettingsLogsFlow()
    }
}