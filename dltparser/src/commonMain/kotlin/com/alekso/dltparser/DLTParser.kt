package com.alekso.dltparser

import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltparser.dlt.Payload
import com.alekso.dltparser.dlt.StandardHeader
import com.alekso.dltparser.dlt.VerbosePayload
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

object DLTParser {

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

    /**
     * https://www.autosar.org/fileadmin/standards/R20-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf
     * https://www.autosar.org/fileadmin/standards/R22-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf - Header Type for protocol version "2"
     */
    suspend fun read(
        progressCallback: (Float) -> Unit,
        inputStream: InputStream
    ): List<DLTMessage> {
        progressCallback.invoke(0f)
        inputStream.use {
            println(Thread.currentThread())
            val bytes = it.readBytes() // todo: Max 2GB
            var logsReadCount = 0
            var i = 0
            val messages = mutableListOf<DLTMessage>()

            while (i < bytes.size && logsReadCount < 5_000) {
                progressCallback.invoke(i.toFloat() / bytes.size.toFloat())
                val signature = bytes.sliceArray(i..i + 3).decodeToString()
                val timeStampSec = bytes.sliceArray(i + 4..i + 7).toInt32l()
                val timeStampUs = bytes.sliceArray(i + 8..i + 11).toInt32l()
                val ecuId = bytes.sliceArray(i + 12..i + 15).decodeToString()

                println(
                    "#$logsReadCount '$signature', timestamp: '${
                        simpleDateFormat.format(
                            timeStampSec * 1000L
                        )
                    }', ecu: '$ecuId'"
                )

                val standardHeader = parseStandardHeader(bytes, i + 16)
                i += 16 + standardHeader.getSize()

                var extendedHeader: ExtendedHeader? = null
                if (standardHeader.headerType.useExtendedHeader) {
                    extendedHeader = parseExtendedHeader(bytes, i)
                    i += extendedHeader.getSize()
                }

                //i += 2
                var payload: Payload? = null
                if (extendedHeader != null && extendedHeader.messageInfo.verbose) {
                    val arguments = mutableListOf<VerbosePayload.Argument>()
                    println("Payload.parse: ${extendedHeader.argumentsCount} payload arguments found")
                    for (j in 0..<extendedHeader.argumentsCount) {
                        val verbosePayloadArgument = parseVerbosePayload(j, bytes, i)
                        arguments.add(verbosePayloadArgument)
                        i += verbosePayloadArgument.getSize()
                    }
                    payload = VerbosePayload(arguments)
                }

                println()

                messages.add(
                    DLTMessage(
                        signature, timeStampSec, timeStampUs, ecuId,
                        standardHeader, extendedHeader,
                        payload
                    )
                )
                logsReadCount++
            }
            return messages
        }
    }

    private fun parseStandardHeader(bytes: ByteArray, i: Int): StandardHeader {
        println("StandardHeader.parse")
        var p = i
        val headerType = parseStandardHeaderType(bytes[p]); p += 1
        val messageCounter = bytes[p]; p += 1
        val length = bytes.sliceArray(p..p + 1).toInt16b(); p += 2
        val ecuId =
            if (headerType.withEcuId) bytes.sliceArray(p..p + 3).decodeToString() else null; p += 4
        val sessionId =
            if (headerType.withSessionId) bytes.sliceArray(p..p + 3).toInt32b() else null; p += 4
        val timeStamp =
            if (headerType.withTimestamp) bytes.sliceArray(p..p + 3).toInt32b() else null; p += 4

        println(
            "   messageCounter: $messageCounter; length: $length: ecuId: '$ecuId', sessionId: $sessionId; timeStamp: ${
                if (timeStamp != null) simpleDateFormat.format(timeStamp * 1000L) else "null"
            }"
        )

        return StandardHeader(
            headerType,
            messageCounter,
            length,
            ecuId,
            sessionId,
            timeStamp
        )
    }

    private fun parseStandardHeaderType(byte: Byte): StandardHeader.HeaderType {
        val useExtendedHeader = byte.isBitSet(0)
        val payloadLittleEndian = byte.isBitSet(1)
        val withEcuId = byte.isBitSet(2)
        val withSessionId = byte.isBitSet(3)
        val withTimestamp = byte.isBitSet(4)
        val versionNumber = (byte.toInt() shr 5).toByte()

        println(
            "   HeaderType.parse: " +
                    "${byte.toHex()} (${byte.toString(2).padStart(8, '0')}) " +
                    "extendedHeader: $useExtendedHeader, payloadLittleEndian: $payloadLittleEndian, withEcuId: $withEcuId, withSessionId: $withSessionId, withTimestamp: $withTimestamp, versionNumber: $versionNumber)"
        )

        return StandardHeader.HeaderType(
            byte,
            useExtendedHeader,
            payloadLittleEndian,
            withEcuId,
            withSessionId,
            withTimestamp,
            versionNumber
        )
    }

    private fun parseExtendedHeader(bytes: ByteArray, i: Int): ExtendedHeader {
        println("ExtendedHeader.parse:")
        var p = i
        val messageInfo = parseMessageInfo(bytes[p]); p += 1
        val argumentsCount = bytes[p].toInt(); p += 1
        val applicationId = bytes.sliceArray(p..p + 3).decodeToString(); p += 4
        val contextId = bytes.sliceArray(p..p + 3).decodeToString(); p += 4

        println("   messageInfo: $messageInfo, argumentsCount: $argumentsCount, applicationId: $applicationId, contextId: $contextId")

        return ExtendedHeader(
            messageInfo,
            argumentsCount,
            applicationId,
            contextId
        )
    }


    fun parseMessageInfo(byte: Byte): MessageInfo {
        val messageType = parseMessageType(byte)

        return MessageInfo(
            originalByte = byte,
            verbose = byte.isBitSet(0),
            messageType = messageType,
            messageTypeInfo = parseMessageTypeInfo(byte, messageType)
        )
    }

    /**
     * MSIN
     * ----|xxx-
     */
    private fun parseMessageType(byte: Byte): MessageInfo.MESSAGE_TYPE {
        val mask = 0b00000111
        val result = (byte.toInt()).shr(1) and mask
        return MessageInfo.MESSAGE_TYPE.entries.first { it.i == result }
    }

    private fun parseMessageTypeInfo(
        byte: Byte,
        messageType: MessageInfo.MESSAGE_TYPE
    ): MessageInfo.MESSAGE_TYPE_INFO {
        val mask = 0b00001111
        val result = (byte.toInt()).shr(4) and mask
        return when (messageType) {
            MessageInfo.MESSAGE_TYPE.DLT_TYPE_LOG -> when (result) {
                1 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_FATAL
                2 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_DLT_ERROR
                3 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_WARN
                4 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_INFO
                5 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_DEBUG
                6 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_VERBOSE
                else -> MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_RESERVED
            }

            MessageInfo.MESSAGE_TYPE.DLT_TYPE_APP_TRACE -> when (result) {
                1 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_TRACE_VARIABLE
                2 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_TRACE_FUNCTION_IN
                3 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_TRACE_FUNCTION_OUT
                4 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_TRACE_STATE
                5 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_TRACE_VFB
                else -> MessageInfo.MESSAGE_TYPE_INFO.DLT_TRACE_RESERVED
            }

            MessageInfo.MESSAGE_TYPE.DLT_TYPE_NW_TRACE -> when (result) {
                1 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_IPC
                2 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_CAN
                3 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_FLEXRAY
                4 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_MOST
                5 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_ETHERNET
                6 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_SOMEIP
                else -> MessageInfo.MESSAGE_TYPE_INFO.DLT_NW_TRACE_USER_DEFINED
            }

            MessageInfo.MESSAGE_TYPE.DLT_TYPE_CONTROL -> when (result) {
                1 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_CONTROL_REQUEST
                2 -> MessageInfo.MESSAGE_TYPE_INFO.DLT_CONTROL_RESPONSE
                else -> MessageInfo.MESSAGE_TYPE_INFO.DLT_CONTROL_RESERVED
            }

            else -> MessageInfo.MESSAGE_TYPE_INFO.UNKNOWN
        }
    }


    private fun parseVerbosePayload(j: Int, bytes: ByteArray, i: Int): VerbosePayload.Argument {

        val typeInfoInt = bytes.sliceArray(i..i + 3).toInt32l()
        val typeInfo = parseVerbosePayloadTypeInfo(typeInfoInt)

        val payloadSizeUnchecked = bytes.sliceArray(i + 4..i + 5).toInt16l().toUByte()
        val payloadSize: Int = if (payloadSizeUnchecked > 0U) payloadSizeUnchecked.toInt() else 1
        val toIndex = i + 6 + payloadSize
        val payload = bytes.sliceArray(i + 6..if (toIndex < bytes.size) toIndex else bytes.size - 1)

//                println("   $j: Argument.parse:")
//                println("       " + bytes.sliceArray(i..i + 20).toHex())
//                println("       typeInfo: $typeInfo")
//                println("       payload size: $payloadSize, content: '${String(payload)}'")

        return VerbosePayload.Argument(
            typeInfoInt,
            typeInfo,
            payloadSize,
            payload
        )
    }

    private fun parseVerbosePayloadTypeInfo(typeInfoInt: Int): VerbosePayload.TypeInfo {
        val mask = 0b0000000000000000_0000000000001111
        val shifted = (typeInfoInt).shr(28)
        val typeLengthBits = when ((typeInfoInt).shr(28) and mask) {
            1 -> 8
            2 -> 16
            3 -> 32
            4 -> 64
            5 -> 128
            else -> 0
        }
//                println("   typeLengthBits : ${typeInfoInt.toString(16).padStart(8, '0')} ")
        println("   typeInfo : ${typeInfoInt.toString(2).padStart(32, '0')} ")
//                println("   shifted        : ${shifted.toString(2).padStart(32, '0')}")
//                println(
//                    "   mask           : ${mask.toString(2).padStart(32, '0')} ->" +
//                            " $typeLengthBits"
//                )

        val stringCodingMask = 0b000000000000000111000000000000000
        val stringCoding = when ((typeInfoInt).shr(15) and stringCodingMask) {
            0 -> VerbosePayload.TypeInfo.STRING_CODING.ASCII
            1 -> VerbosePayload.TypeInfo.STRING_CODING.UTF8
            else -> VerbosePayload.TypeInfo.STRING_CODING.RESERVED
        }
        return VerbosePayload.TypeInfo(
            typeLengthBits,
            typeBool = typeInfoInt.isBitSet(4),
            typeSigned = typeInfoInt.isBitSet(5),
            typeUnsigned = typeInfoInt.isBitSet(6),
            typeFloat = typeInfoInt.isBitSet(7),
            typeArray = typeInfoInt.isBitSet(8),
            typeString = typeInfoInt.isBitSet(9),
            typeRaw = typeInfoInt.isBitSet(10),
            variableInfo = typeInfoInt.isBitSet(11),
            fixedPoint = typeInfoInt.isBitSet(12),
            traceInfo = typeInfoInt.isBitSet(13),
            typeStruct = typeInfoInt.isBitSet(14),
            stringCoding = stringCoding
        )
    }

}