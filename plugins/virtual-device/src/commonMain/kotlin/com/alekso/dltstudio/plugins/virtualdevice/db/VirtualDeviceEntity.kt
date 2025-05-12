package com.alekso.dltstudio.plugins.virtualdevice.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VirtualDeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val width: Int,
    val height: Int,
)