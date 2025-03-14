package com.alekso.dltstudio.db.preferences

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alekso.dltstudio.model.Column
import com.alekso.dltstudio.model.ColumnParams

@Entity
data class ColumnParamsEntity(
    @PrimaryKey
    val key: String,
    val visible: Boolean,
    val size: Float,
)

fun ColumnParamsEntity.toColumnParams() =
    ColumnParams(
        column = Column.valueOf(key),
        visible = visible,
        size = size
    )

fun ColumnParams.toColumnParamsEntity() =
    ColumnParamsEntity(key = column.name, visible = visible, size = size)
