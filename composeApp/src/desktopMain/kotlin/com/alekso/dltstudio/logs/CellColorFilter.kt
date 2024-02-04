package com.alekso.dltstudio.logs

import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.MessageInfo

data class CellColorFilter(
    val condition: (DLTMessage) -> Boolean,
    val cellStyle: CellStyle
)

val ColorFilterWarn = CellColorFilter(
    condition = { msg ->
        msg.extendedHeader?.messageInfo?.messageTypeInfo == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_WARN
    }, cellStyle = CellStyle(backgroundColor = Color.Yellow)
)
val ColorFilterError = CellColorFilter(
    condition = { msg ->
        msg.extendedHeader?.messageInfo?.messageTypeInfo == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_DLT_ERROR
    }, cellStyle = CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)
)
val ColorFilterFatal = CellColorFilter(
    condition = { msg ->
        msg.extendedHeader?.messageInfo?.messageTypeInfo == MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_FATAL
    }, cellStyle = CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)
)
