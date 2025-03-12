package com.alekso.dltstudio.model

enum class Columns {
    Mark,
    RowNumber,
    DateTime,
    Time,
    MessageCount,
    EcuId,
    SessionId,
    AppId,
    CtxId,
    LogType,
}

data class ColumnsParams(
    val key: Columns,
    val title: String,
    val name: String,
    val visible: Boolean,
    val size: Float,
) {
    companion object {
        val DefaultParams = listOf<ColumnsParams>(
            ColumnsParams(Columns.Mark, "", "Mark", true, 20f),
            ColumnsParams(Columns.RowNumber, "#", "Row number", true, 54f),
            ColumnsParams(Columns.DateTime, "DateTime", "DateTime", true, 180f),
            ColumnsParams(Columns.Time, "Time", "Time", true, 80f),
            ColumnsParams(Columns.MessageCount, "Count", "Count", true, 40f),
            ColumnsParams(Columns.EcuId, "EcuId", "EcuId", true, 46f),
            ColumnsParams(Columns.SessionId, "SessionId", "SessionId", true, 46f),
            ColumnsParams(Columns.AppId, "AppId", "AppId", true, 46f),
            ColumnsParams(Columns.CtxId, "CtxId", "CtxId", true, 46f),
            ColumnsParams(Columns.LogType, "", "Log type", true, 14f),
        )
    }
}