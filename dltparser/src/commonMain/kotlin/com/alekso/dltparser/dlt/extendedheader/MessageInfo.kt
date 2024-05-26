package com.alekso.dltparser.dlt.extendedheader


data class MessageInfo(
    val originalByte: Byte,
    val verbose: Boolean,
    val messageType: MessageType,
    val messageTypeInfo: MessageTypeInfo,
) {



    companion object {

        fun messageTypeInfoFromByte(byte: Byte): MessageType {
            val mask = 0b00000111
            val result = (byte.toInt()).shr(1) and mask
            return MessageType.fromVal(result)
        }

        fun messageTypeInfoFromByte(byte: Byte, messageType: MessageType): MessageTypeInfo {
            val mask = 0b00001111
            val result = (byte.toInt()).shr(4) and mask
            return when (messageType) {
                MessageType.DLT_TYPE_LOG -> when (result) {
                    1 -> MessageTypeInfo.DLT_LOG_FATAL
                    2 -> MessageTypeInfo.DLT_LOG_DLT_ERROR
                    3 -> MessageTypeInfo.DLT_LOG_WARN
                    4 -> MessageTypeInfo.DLT_LOG_INFO
                    5 -> MessageTypeInfo.DLT_LOG_DEBUG
                    6 -> MessageTypeInfo.DLT_LOG_VERBOSE
                    else -> MessageTypeInfo.DLT_LOG_RESERVED
                }

                MessageType.DLT_TYPE_APP_TRACE -> when (result) {
                    1 -> MessageTypeInfo.DLT_TRACE_VARIABLE
                    2 -> MessageTypeInfo.DLT_TRACE_FUNCTION_IN
                    3 -> MessageTypeInfo.DLT_TRACE_FUNCTION_OUT
                    4 -> MessageTypeInfo.DLT_TRACE_STATE
                    5 -> MessageTypeInfo.DLT_TRACE_VFB
                    else -> MessageTypeInfo.DLT_TRACE_RESERVED
                }

                MessageType.DLT_TYPE_NW_TRACE -> when (result) {
                    1 -> MessageTypeInfo.DLT_NW_TRACE_IPC
                    2 -> MessageTypeInfo.DLT_NW_TRACE_CAN
                    3 -> MessageTypeInfo.DLT_NW_TRACE_FLEXRAY
                    4 -> MessageTypeInfo.DLT_NW_TRACE_MOST
                    5 -> MessageTypeInfo.DLT_NW_TRACE_ETHERNET
                    6 -> MessageTypeInfo.DLT_NW_TRACE_SOMEIP
                    else -> MessageTypeInfo.DLT_NW_TRACE_USER_DEFINED
                }

                MessageType.DLT_TYPE_CONTROL -> when (result) {
                    1 -> MessageTypeInfo.DLT_CONTROL_REQUEST
                    2 -> MessageTypeInfo.DLT_CONTROL_RESPONSE
                    else -> MessageTypeInfo.DLT_CONTROL_RESERVED
                }

                else -> MessageTypeInfo.UNKNOWN
            }
        }

    }

}