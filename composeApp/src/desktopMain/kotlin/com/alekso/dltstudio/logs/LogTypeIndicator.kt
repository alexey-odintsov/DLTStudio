package com.alekso.dltstudio.logs

import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltstudio.logs.colorfilters.ColorFilterError
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal
import com.alekso.dltstudio.logs.colorfilters.ColorFilterWarn

data class LogTypeIndicator(
    val logTypeSymbol: String = "",
    val logTypeStyle: CellStyle? = null,
) {
    companion object {
        fun fromMessageType(messageTypeInfo: MessageInfo.MessageTypeInfo?): LogTypeIndicator? {
            return when (messageTypeInfo) {
                MessageInfo.MessageTypeInfo.DLT_LOG_FATAL -> LogTypeIndicator(
                    "F",
                    ColorFilterFatal.cellStyle
                )

                MessageInfo.MessageTypeInfo.DLT_LOG_DLT_ERROR -> LogTypeIndicator(
                    "E",
                    ColorFilterError.cellStyle
                )

                MessageInfo.MessageTypeInfo.DLT_LOG_WARN -> LogTypeIndicator(
                    "W",
                    ColorFilterWarn.cellStyle
                )

                MessageInfo.MessageTypeInfo.DLT_LOG_INFO -> LogTypeIndicator("I")
                MessageInfo.MessageTypeInfo.DLT_LOG_DEBUG -> LogTypeIndicator("D")
                MessageInfo.MessageTypeInfo.DLT_LOG_VERBOSE -> LogTypeIndicator("V")
                else -> null
            }
        }

    }
}

