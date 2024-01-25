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
        DLT_TYPE_RESERVED_7(7),
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

}