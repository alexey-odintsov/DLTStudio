package com.alekso.dltstudio.plugins.diagramtimeline.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {

    suspend fun addNewRecentTimelineFilter(item: RecentTimelineFilterFileEntry) {
        removeRecentTimelineFilter(item.fileName)
        addRecentTimelineFilter(item)
        ensureRecentTimelineFilterCapacity()
    }

    @Upsert
    suspend fun addRecentTimelineFilter(item: RecentTimelineFilterFileEntry)

    @Query("SELECT * FROM RecentTimelineFilterFileEntry LIMIT $MAX_RECENT_TIMELINE_FILTERS")
    fun getRecentTimelineFilterFlow(): Flow<List<RecentTimelineFilterFileEntry>>

    @Query("DELETE FROM RecentTimelineFilterFileEntry WHERE id NOT IN (SELECT id FROM RecentTimelineFilterFileEntry ORDER BY id DESC LIMIT $MAX_RECENT_TIMELINE_FILTERS)")
    suspend fun ensureRecentTimelineFilterCapacity()

    @Query("DELETE FROM RecentTimelineFilterFileEntry WHERE fileName = :value")
    suspend fun removeRecentTimelineFilter(value: String)

    companion object {
        const val MAX_RECENT_TIMELINE_FILTERS = 10
    }
}