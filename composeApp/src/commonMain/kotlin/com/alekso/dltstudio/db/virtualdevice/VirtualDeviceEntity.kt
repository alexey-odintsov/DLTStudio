package com.alekso.dltstudio.db.virtualdevice

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VirtualDeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val width: Float,
    val height: Float,
)