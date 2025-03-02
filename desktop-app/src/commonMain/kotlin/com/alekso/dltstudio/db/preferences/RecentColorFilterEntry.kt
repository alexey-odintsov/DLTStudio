package com.alekso.dltstudio.db.preferences

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentColorFilterEntry(
    val fileName: String,
    val path: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // we use it for ordering
)
