package com.alekso.dltstudio.plugins.diagramtimeline.db

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface TimelineRepository {
    suspend fun addNewRecentTimelineFilter(item: RecentTimelineFilterFileEntry)
    fun getRecentTimelineFilters(): Flow<List<RecentTimelineFilterFileEntry>>
}

class TimelineRepositoryImpl(
    private val database: TimelineDatabase,
    private val scope: CoroutineScope
) : TimelineRepository {

    override suspend fun addNewRecentTimelineFilter(item: RecentTimelineFilterFileEntry) {
        database.getDao().addNewRecentTimelineFilter(item)
    }

    override fun getRecentTimelineFilters(): Flow<List<RecentTimelineFilterFileEntry>> {
        return database.getDao().getRecentTimelineFilterFlow()
    }
}