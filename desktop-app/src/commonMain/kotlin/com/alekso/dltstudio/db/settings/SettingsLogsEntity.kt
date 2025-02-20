package com.alekso.dltstudio.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingsLogsEntity(
    val backendType: Int,
    @PrimaryKey
    val id: Int = 0,
)