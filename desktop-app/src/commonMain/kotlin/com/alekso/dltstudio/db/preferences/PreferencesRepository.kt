package com.alekso.dltstudio.db.preferences

import com.alekso.dltstudio.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun addNewSearch(item: SearchEntity)
    fun getRecentSearch(): Flow<List<SearchEntity>>
    suspend fun addNewRecentColorFilter(item: RecentColorFilterEntry)
    fun getRecentColorFilters(): Flow<List<RecentColorFilterEntry>>
    suspend fun addNewRecentTimelineFilter(item: RecentTimelineEntry)
    fun getRecentTimelineFilters(): Flow<List<RecentTimelineEntry>>
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

    override suspend fun addNewRecentColorFilter(item: RecentColorFilterEntry) {
        database.getPreferencesDao().addNewRecentColorFilter(item)
    }

    override fun getRecentColorFilters(): Flow<List<RecentColorFilterEntry>> {
        return database.getPreferencesDao().getRecentRecentColorFilterFlow()
    }

    override suspend fun addNewRecentTimelineFilter(item: RecentTimelineEntry) {
        database.getPreferencesDao().addRecentTimelineFilter(item)
    }

    override fun getRecentTimelineFilters(): Flow<List<RecentTimelineEntry>> {
        return database.getPreferencesDao().getRecentTimelineFilterFlow()
    }

}