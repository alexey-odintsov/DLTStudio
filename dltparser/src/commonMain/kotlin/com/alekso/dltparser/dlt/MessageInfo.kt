package com.alekso.dltparser.dlt


data class MessageInfo(
    val originalByte: Byte,
    val verbose: Boolean,
    val messageType: MESSAGE_TYPE,
    val messageTypeInfo: MESSAGE_TYPE_INFO,
) {

    enum class MESSAGE_TYPE(val i: Int) {
        DLT_TYPE_LOG(0),
        DLT_TYPE_APP_TRACE(1),
        DLT_TYPE_NW_TRACE(2),
        DLT_TYPE_CONTROL(3),
        DLT_TYPE_RESERVED_4(4),
        DLT_TYPE_RESERVED_5(5),
        DLT_TYPE_RESERVED_6(6),
        DLT_TYPE_RESERVED_7(7);

        companion object {
            fun fromVal(i: Int): MESSAGE_TYPE {
                return entries.first { it.i == i }
            }
        }

    }

    enum class MESSAGE_TYPE_INFO {
        DLT_LOG_FATAL,
        DLT_LOG_DLT_ERROR,
        DLT_LOG_WARN,
        DLT_LOG_INFO,
        DLT_LOG_DEBUG,
        DLT_LOG_VERBOSE,
        DLT_LOG_RESERVED,

        DLT_TRACE_VARIABLE,
        DLT_TRACE_FUNCTION_IN,
        DLT_TRACE_FUNCTION_OUT,
        DLT_TRACE_STATE,
        DLT_TRACE_VFB,
        DLT_TRACE_RESERVED,

        DLT_NW_TRACE_IPC,
        DLT_NW_TRACE_CAN,
        DLT_NW_TRACE_FLEXRAY,
        DLT_NW_TRACE_MOST,
        DLT_NW_TRACE_ETHERNET,
        DLT_NW_TRACE_SOMEIP,
        DLT_NW_TRACE_USER_DEFINED,

        DLT_CONTROL_REQUEST,
        DLT_CONTROL_RESPONSE,
        DLT_CONTROL_RESERVED,

        UNKNOWN,
    }

    companion object {

        fun messageTypeInfoFromByte(byte: Byte): MESSAGE_TYPE {
            val mask = 0b00000111
            val result = (byte.toInt()).shr(1) and mask
            return MESSAGE_TYPE.fromVal(result)
        }

        fun messageTypeInfoFromByte(byte: Byte, messageType: MESSAGE_TYPE): MESSAGE_TYPE_INFO {
            val mask = 0b00001111
            val result = (byte.toInt()).shr(4) and mask
            return when (messageType) {
                MESSAGE_TYPE.DLT_TYPE_LOG -> when (result) {
                    1 -> MESSAGE_TYPE_INFO.DLT_LOG_FATAL
                    2 -> MESSAGE_TYPE_INFO.DLT_LOG_DLT_ERROR
                    3 -> MESSAGE_TYPE_INFO.DLT_LOG_WARN
                    4 -> MESSAGE_TYPE_INFO.DLT_LOG_INFO
                    5 -> MESSAGE_TYPE_INFO.DLT_LOG_DEBUG
                    6 -> MESSAGE_TYPE_INFO.DLT_LOG_VERBOSE
                    else -> MESSAGE_TYPE_INFO.DLT_LOG_RESERVED
                }

                MESSAGE_TYPE.DLT_TYPE_APP_TRACE -> when (result) {
                    1 -> MESSAGE_TYPE_INFO.DLT_TRACE_VARIABLE
                    2 -> MESSAGE_TYPE_INFO.DLT_TRACE_FUNCTION_IN
                    3 -> MESSAGE_TYPE_INFO.DLT_TRACE_FUNCTION_OUT
                    4 -> MESSAGE_TYPE_INFO.DLT_TRACE_STATE
                    5 -> MESSAGE_TYPE_INFO.DLT_TRACE_VFB
                    else -> MESSAGE_TYPE_INFO.DLT_TRACE_RESERVED
                }

                MESSAGE_TYPE.DLT_TYPE_NW_TRACE -> when (result) {
                    1 -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_IPC
                    2 -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_CAN
                    3 -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_FLEXRAY
                    4 -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_MOST
                    5 -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_ETHERNET
                    6 -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_SOMEIP
                    else -> MESSAGE_TYPE_INFO.DLT_NW_TRACE_USER_DEFINED
                }

                MESSAGE_TYPE.DLT_TYPE_CONTROL -> when (result) {
                    1 -> MESSAGE_TYPE_INFO.DLT_CONTROL_REQUEST
                    2 -> MESSAGE_TYPE_INFO.DLT_CONTROL_RESPONSE
                    else -> MESSAGE_TYPE_INFO.DLT_CONTROL_RESERVED
                }

                else -> MESSAGE_TYPE_INFO.UNKNOWN
            }
        }

    }

}