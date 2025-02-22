package com.alekso.dltstudio.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingsUIEntity(
    val fontSize: Int,
    val fontType: Int,
    @PrimaryKey
    val id: Int = 0,
)