package com.alekso.dltstudio.model

enum class Column(
    val title: String,
    val menuName: String,
) {
    Mark("", "Mark"),
    RowNumber("#", "Row number"),
    DateTime("DateTime", "DateTime"),
    Time("Time", "Time"),
    MessageCount("Count", "Message count"),
    EcuId("EcuId", "EcuId"),
    SessionId("SessionId", "SessionID"),
    AppId("AppId", "AppId"),
    CtxId("ContextId", "ContextId"),
    LogType("", "Log type"),
}

data class ColumnParams(
    val column: Column,
    val visible: Boolean,
    val size: Float,
) {
    companion object {
        val DefaultParams = listOf<ColumnParams>(
            ColumnParams(Column.Mark, true, 20f),
            ColumnParams(Column.RowNumber, true, 54f),
            ColumnParams(Column.DateTime, true, 180f),
            ColumnParams(Column.Time, true, 80f),
            ColumnParams(Column.MessageCount, true, 40f),
            ColumnParams(Column.EcuId, true, 46f),
            ColumnParams(Column.SessionId, true, 46f),
            ColumnParams(Column.AppId, true, 46f),
            ColumnParams(Column.CtxId, true, 46f),
            ColumnParams(Column.LogType, true, 14f),
        )
    }
}