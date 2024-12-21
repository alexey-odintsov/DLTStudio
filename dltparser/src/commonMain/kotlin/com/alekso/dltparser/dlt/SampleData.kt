package com.alekso.dltparser.dlt

import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.extendedheader.MessageInfo
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.standardheader.HeaderType
import com.alekso.dltparser.dlt.standardheader.StandardHeader
import com.alekso.dltparser.dlt.verbosepayload.Argument
import com.alekso.dltparser.dlt.verbosepayload.TypeInfo
import com.alekso.dltparser.dlt.verbosepayload.VerbosePayload

object SampleData {
    fun getSampleDltMessages(size: Int): List<DLTMessage> {
        val list = mutableListOf<DLTMessage>()

        for (i in 0..size) {
            val dltMessage = StringDLTMessage(
                21142234 + i.toLong(), "MGUA",
                StandardHeader(
                    HeaderType(0.toByte(), true, true, true, true, true, 1),
                    10.toUByte(), 10U, "MGUA", 443, 332422U
                ),
                ExtendedHeader(
                    MessageInfo(
                        30.toByte(),
                        true,
                        MessageType.DLT_TYPE_APP_TRACE,
                        MessageTypeInfo.DLT_LOG_INFO
                    ), 2U, "APP", "CTX"
                ),
                VerbosePayload(
                    listOf(
                        Argument(
                            1,
                            TypeInfo(
                                1,
                                false,
                                false,
                                false,
                                false,
                                false,
                                false,
                                true,
                                false,
                                false,
                                false,
                                false,
                                TypeInfo.StringCoding.UTF8
                            ), 12, 10, "TEST MESSAGE".toByteArray()
                        )
                    )
                ).asText().toByteArray(),
                122
            )
            list.add(dltMessage)
        }

        return list
    }
}