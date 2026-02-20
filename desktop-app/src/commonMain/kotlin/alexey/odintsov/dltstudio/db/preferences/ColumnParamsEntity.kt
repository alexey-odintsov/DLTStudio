package alexey.odintsov.dltstudio.db.preferences

import alexey.odintsov.dltstudio.model.Column
import alexey.odintsov.dltstudio.model.ColumnParams
import androidx.room.Entity
import androidx.room.PrimaryKey

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
