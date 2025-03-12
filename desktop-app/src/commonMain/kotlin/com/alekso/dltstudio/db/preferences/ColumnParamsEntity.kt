package com.alekso.dltstudio.db.preferences

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alekso.dltstudio.model.Columns
import com.alekso.dltstudio.model.ColumnsParams

@Entity
data class ColumnParamsEntity(
    @PrimaryKey
    val key: String,
    val visible: Boolean,
    val size: Float,
)

fun ColumnParamsEntity.toColumnParams() =
    ColumnsParams(
        key = Columns.valueOf(key),
        title = "?",
        name = "?",
        visible = visible,
        size = size
    )

fun ColumnsParams.toColumnParamsEntity() =
    ColumnParamsEntity(key = key.name, visible = visible, size = size)
