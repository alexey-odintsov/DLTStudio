package com.alekso.dltstudio.plugins.diagramtimeline.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentTimelineFilterFileEntry(
    val fileName: String,
    val path: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // we use it for ordering
)
