package com.alekso.dltstudio.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingsLogsEntity(
    val backendType: Int,
    val defaultLogsFolderPath: String?,
    val defaultColorFiltersFolderPath: String?,
    @PrimaryKey
    val id: Int = 0,
)