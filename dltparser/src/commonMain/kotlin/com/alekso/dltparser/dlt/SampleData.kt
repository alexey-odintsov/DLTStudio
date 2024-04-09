package com.alekso.dltparser.dlt

object SampleData {
    fun getSampleDltMessages(size: Int): List<DLTMessage> {
        val list = mutableListOf<DLTMessage>()

        for (i in 0..size) {
            val dltMessage = DLTMessage(
                21142234, "MGUA",
                StandardHeader(
                    StandardHeader.HeaderType(0.toByte(), true, true, true, true, true, 1),
                    10.toUByte(), 10U, "MGUA", 443, 332422U
                ),
                ExtendedHeader(
                    MessageInfo(
                        30.toByte(),
                        true,
                        MessageInfo.MessageType.DLT_TYPE_APP_TRACE,
                        MessageInfo.MessageTypeInfo.DLT_LOG_INFO
                    ), 2U, "APP", "CTX"
                ),
                VerbosePayload(
                    listOf(
                        VerbosePayload.Argument(
                            1,
                            VerbosePayload.TypeInfo(
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
                                VerbosePayload.TypeInfo.StringCoding.UTF8
                            ), 12, 10, "TEST MESSAGE".toByteArray()
                        )
                    )
                ).asText(),
                122
            )
            list.add(dltMessage)
        }

        return list
    }
}