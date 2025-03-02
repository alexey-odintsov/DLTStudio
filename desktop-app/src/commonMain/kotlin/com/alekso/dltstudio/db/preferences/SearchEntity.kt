package com.alekso.dltstudio.db.preferences

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchEntity(
    val value: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // we use it for ordering
)