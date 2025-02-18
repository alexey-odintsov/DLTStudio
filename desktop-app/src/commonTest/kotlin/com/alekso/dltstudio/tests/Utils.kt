package com.alekso.dltstudio.tests

import com.alekso.dltmessage.DLTMessage
import com.alekso.dltmessage.StructuredDLTMessage
import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.extendedheader.MessageInfo
import com.alekso.dltmessage.extendedheader.MessageType
import com.alekso.dltmessage.extendedheader.MessageTypeInfo
import com.alekso.dltmessage.standardheader.HeaderType
import com.alekso.dltmessage.standardheader.StandardHeader
import com.alekso.dltmessage.verbosepayload.Argument
import com.alekso.dltmessage.verbosepayload.TypeInfo
import com.alekso.dltmessage.verbosepayload.VerbosePayload

object Utils {
    fun dltMessage(
        timeStampNano: Long = System.nanoTime(),
        ecuId: String = "ECU1",
        appId: String = "App",
        contextId: String = "Context",
        payload: String = "Test payload",
    ): DLTMessage {
        return StructuredDLTMessage(
            timeStampNano,
            StandardHeader(
                HeaderType(
                    61.toByte(),
                    useExtendedHeader = true,
                    payloadBigEndian = false,
                    withEcuId = true,
                    withSessionId = true,
                    withTimestamp = true,
                    versionNumber = 1
                ),
                25.toUByte(), 68U, ecuId, 61, 184566085U
            ),
            ExtendedHeader(
                MessageInfo(
                    67.toByte(),
                    true,
                    MessageType.DLT_TYPE_APP_TRACE,
                    MessageTypeInfo.DLT_TRACE_STATE
                ), 85U, appId, contextId
            ),
            VerbosePayload(
                listOf(
                    Argument(
                        512,
                        TypeInfo(
                            typeString = true,
                            stringCoding = TypeInfo.StringCoding.ASCII
                        ),
                        2,
                        63,
                        payload.toByteArray()
                    )
                )
            )
        )
    }
}