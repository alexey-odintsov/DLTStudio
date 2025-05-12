package com.alekso.dltstudio.plugins.diagramtimeline.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RecentTimelineFilterFileEntry::class,
    ],
    version = 1, exportSchema = true,
)
abstract class TimelineDatabase : RoomDatabase() {
    abstract fun getDao(): TimelineDao
}
