package com.alekso.dltstudio.logs.colorfilters

import androidx.compose.ui.graphics.Color
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltstudio.logs.CellStyle

enum class FilterParameter {
    Payload,
    EcuId,
    AppId,
    ContextId,
    SessionId,
    MessageType,
    MessageTypeInfo,
}

data class ColorFilter(
    val name: String,
    val filters: Map<FilterParameter, String>,
    val cellStyle: CellStyle,
    val enabled: Boolean = true,
) {
    fun assess(message: DLTMessage): Boolean {
        return filters.all {
            return@all enabled && when (it.key) {
                FilterParameter.MessageType -> {
                    message.extendedHeader?.messageInfo?.messageType?.name == it.value
                }

                FilterParameter.MessageTypeInfo -> {
                    message.extendedHeader?.messageInfo?.messageTypeInfo?.name == it.value
                }

                FilterParameter.EcuId -> {
                    message.standardHeader.ecuId == it.value
                }

                FilterParameter.ContextId -> {
                    message.extendedHeader?.contextId == it.value
                }

                FilterParameter.AppId -> {
                    message.extendedHeader?.applicationId == it.value
                }

                FilterParameter.SessionId -> {
                    message.standardHeader.sessionId == it.value.toInt()
                }

                FilterParameter.Payload -> {
                    message.payload?.asText()?.contains(it.value) ?: false
                }

                else -> false
            }
        }

    }

    companion object {
        val Empty = ColorFilter("New filter", mutableMapOf(), CellStyle.Default)
    }
}

val ColorFilterWarn = ColorFilter(
    "Warn",
    mapOf(FilterParameter.MessageTypeInfo to MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_WARN.name),
    CellStyle(backgroundColor = Color.Yellow)
)
val ColorFilterError = ColorFilter(
    "Error",
    mapOf(FilterParameter.MessageTypeInfo to MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_DLT_ERROR.name),
    CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)
)
val ColorFilterFatal = ColorFilter(
    "Fatal",
    mapOf(FilterParameter.MessageTypeInfo to MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_FATAL.name),
    CellStyle(backgroundColor = Color(0xE7, 0x62, 0x29), textColor = Color.White)
)
