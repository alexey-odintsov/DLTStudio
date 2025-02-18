package com.alekso.dltstudio.logs

import com.alekso.dltmessage.extendedheader.MessageTypeInfo
import com.alekso.dltstudio.logs.colorfilters.ColorFilterError
import com.alekso.dltstudio.logs.colorfilters.ColorFilterFatal
import com.alekso.dltstudio.logs.colorfilters.ColorFilterWarn

data class LogTypeIndicator(
    val logTypeSymbol: String = "",
    val logTypeStyle: CellStyle? = null,
) {
    companion object {
        fun fromMessageType(messageTypeInfo: MessageTypeInfo?): LogTypeIndicator? {
            return when (messageTypeInfo) {
                MessageTypeInfo.DLT_LOG_FATAL -> LogTypeIndicator(
                    "F",
                    ColorFilterFatal.cellStyle
                )

                MessageTypeInfo.DLT_LOG_DLT_ERROR -> LogTypeIndicator(
                    "E",
                    ColorFilterError.cellStyle
                )

                MessageTypeInfo.DLT_LOG_WARN -> LogTypeIndicator(
                    "W",
                    ColorFilterWarn.cellStyle
                )

                MessageTypeInfo.DLT_LOG_INFO -> LogTypeIndicator("I")
                MessageTypeInfo.DLT_LOG_DEBUG -> LogTypeIndicator("D")
                MessageTypeInfo.DLT_LOG_VERBOSE -> LogTypeIndicator("V")
                else -> null
            }
        }

    }
}

