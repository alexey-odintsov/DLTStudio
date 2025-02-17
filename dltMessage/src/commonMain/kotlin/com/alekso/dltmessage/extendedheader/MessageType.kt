package com.alekso.dltmessage.extendedheader

enum class MessageType(val i: Int) {
    DLT_TYPE_LOG(0),
    DLT_TYPE_APP_TRACE(1),
    DLT_TYPE_NW_TRACE(2),
    DLT_TYPE_CONTROL(3),
    DLT_TYPE_RESERVED_4(4),
    DLT_TYPE_RESERVED_5(5),
    DLT_TYPE_RESERVED_6(6),
    DLT_TYPE_RESERVED_7(7);

    companion object {
        fun fromVal(i: Int): MessageType {
            return entries.first { it.i == i }
        }
    }

}