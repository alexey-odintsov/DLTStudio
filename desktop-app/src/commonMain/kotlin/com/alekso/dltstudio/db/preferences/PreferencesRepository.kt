package com.alekso.dltstudio.db.preferences

import com.alekso.dltstudio.db.AppDatabase
import com.alekso.dltstudio.model.ColumnParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun addNewSearch(item: SearchEntity)
    fun getRecentSearch(): Flow<List<SearchEntity>>
    suspend fun addNewRecentColorFilter(item: RecentColorFilterFileEntry)
    fun getRecentColorFilters(): Flow<List<RecentColorFilterFileEntry>>
    suspend fun addNewRecentTimelineFilter(item: RecentTimelineFilterFileEntry)
    fun getRecentTimelineFilters(): Flow<List<RecentTimelineFilterFileEntry>>
    fun getColumnParams(): Flow<List<ColumnParamsEntity>>
    suspend fun updateColumnParams(item: ColumnParams)
    suspend fun resetColumnsParams()
}

class PreferencesRepositoryImpl(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) : PreferencesRepository {
    override suspend fun addNewSearch(item: SearchEntity) {
        database.getPreferencesDao().addNewSearch(item)
    }

    override fun getRecentSearch(): Flow<List<SearchEntity>> {
        return database.getPreferencesDao().getRecentSearchFlow()
    }

    override suspend fun addNewRecentColorFilter(item: RecentColorFilterFileEntry) {
        database.getPreferencesDao().addNewRecentColorFilter(item)
    }

    override fun getRecentColorFilters(): Flow<List<RecentColorFilterFileEntry>> {
        return database.getPreferencesDao().getRecentColorFilterFlow()
    }

    override suspend fun addNewRecentTimelineFilter(item: RecentTimelineFilterFileEntry) {
        database.getPreferencesDao().addNewRecentTimelineFilter(item)
    }

    override fun getRecentTimelineFilters(): Flow<List<RecentTimelineFilterFileEntry>> {
        return database.getPreferencesDao().getRecentTimelineFilterFlow()
    }

    override fun getColumnParams(): Flow<List<ColumnParamsEntity>> {
        return database.getPreferencesDao().getColumnsParamsFlow()
    }

    override suspend fun updateColumnParams(item: ColumnParams) {
        database.getPreferencesDao().updateColumnParams(item.toColumnParamsEntity())
    }

    override suspend fun resetColumnsParams() {
        database.getPreferencesDao().removeColumnsParams()
    }
}