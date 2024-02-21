package com.alekso.dltparser

import com.alekso.dltparser.dlt.ControlMessagePayload
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltparser.dlt.NonVerbosePayload
import com.alekso.dltparser.dlt.Payload
import com.alekso.dltparser.dlt.StandardHeader
import com.alekso.dltparser.dlt.VerbosePayload
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


object DLTParser {
    private const val DEBUG_LOG = true // WARNING: Logging drastically slow down parsing!!!
    private const val MAX_BYTES_TO_READ_DEBUG = -1 // put -1 to ignore
    private const val DLT_HEADER_SIZE_BYTES = 16
    private const val STRING_CODING_MASK = 0b00000000000000000000000000000111
    private val STANDARD_HEADER_ENDIAN = Endian.BIG
    private val EXTENDED_HEADER_ENDIAN = Endian.BIG
    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)

    /**
     * https://www.autosar.org/fileadmin/standards/R20-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf
     * https://www.autosar.org/fileadmin/standards/R22-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf - Header Type for protocol version "2"
     */
    suspend fun read(
        progressCallback: (Float) -> Unit, files: List<File>
    ): List<DLTMessage> {
        progressCallback.invoke(0f)
        val messages = mutableListOf<DLTMessage>()
        var logsReadCount = 0
        var skippedBytes = 0
        val totalSize = files.sumOf { it.length() }
        var bytesRead: Long = 0

        // todo: What to do with non-DLT files? silently skip?
        println("Total ${files.size} file(s) with size: $totalSize bytes")
        files.forEach { file ->

            println("Parsing '$file'")
            file.inputStream().use {

                val bytes = it.readBytes() // todo: Max 2GB
                var i = 0
                var shouldLog: Boolean

                while (i < bytes.size - DLT_HEADER_SIZE_BYTES && (MAX_BYTES_TO_READ_DEBUG < 0 || i < MAX_BYTES_TO_READ_DEBUG)) {
                    shouldLog = false //logsReadCount == 3

                    // Skip until 'DLT' signature found
                    while (!(bytes[i].toInt() == 0x44 && bytes[i + 1].toInt() == 0x4C && bytes[i + 2].toInt() == 0x54 && bytes[i + 3].toInt() == 0x01) && i < (bytes.size - 17)) {
                        i++
                        skippedBytes++
                    }

                    try {
                        if (DEBUG_LOG && shouldLog) {
                            println("#$logsReadCount")
                        }
                        val dltMessage = parseDLTMessage(bytes, i, shouldLog)
                        //if (dltMessage.extendedHeader?.applicationId == "PLAT" && dltMessage.extendedHeader?.contextId == "KVSS") {// applicationId=PLAT, contextId=KVSS
                        messages.add(dltMessage)
                        //}
                        i += dltMessage.sizeBytes // skip read bytes
                        logsReadCount++
                    } catch (e: Exception) {
                        i++ // move counter to the next byte
                        skippedBytes++
                        println(e)
                    }
                    progressCallback.invoke((bytesRead.toFloat() + i) / totalSize)
                }
                bytesRead += i
            }
        }
        println("Parsing complete with $logsReadCount messages; $bytesRead bytes read and $skippedBytes skipped bytes")
        return messages.sortedBy { it.timeStampNano }
    }

    public fun parseDLTMessage(bytes: ByteArray, offset: Int, shouldLog: Boolean): DLTMessage {
        var i = offset
        val timeStampSec = bytes.readInt(i + 4, Endian.LITTLE)
        val timeStampUs = bytes.readInt(i + 8, Endian.LITTLE)
        val timeStampNano = timeStampSec * 1000000L + timeStampUs
        val ecuId = bytes.readString(i + 12, 4).replace("\u0000", "")
        i += DLT_HEADER_SIZE_BYTES

        if (DEBUG_LOG && shouldLog) {
            println(
                "timestamp: '${
                    simpleDateFormat.format(
                        timeStampSec * 1000L
                    )
                }', ecu: '$ecuId'"
            )
        }
        val standardHeader = parseStandardHeader(shouldLog, bytes, i)
        i += standardHeader.getSize()

        var extendedHeader: ExtendedHeader? = null
        if (standardHeader.headerType.useExtendedHeader) {
            extendedHeader = parseExtendedHeader(shouldLog, bytes, i)
            i += extendedHeader.getSize()
        }

        var payload: Payload? = null
        if (extendedHeader != null) {
            if (extendedHeader.messageInfo.verbose) {
                val arguments = mutableListOf<VerbosePayload.Argument>()
                if (DEBUG_LOG && shouldLog) {
                    println(
                        "Payload.parse: ${extendedHeader.argumentsCount} payload arguments found"
                    )
                }
                val payloadSize =
                    (standardHeader.length.toLong() - standardHeader.getSize() - extendedHeader.getSize()).toInt()
                var payloadSelfSize = 0
                try {
                    for (j in 0..<extendedHeader.argumentsCount.toInt()) {

                        val verbosePayloadArgument = parseVerbosePayload(
                            shouldLog,
                            j,
                            bytes,
                            i + payloadSelfSize,
                            if (standardHeader.headerType.payloadBigEndian) Endian.BIG else Endian.LITTLE
                        )
                        arguments.add(verbosePayloadArgument)
                        payloadSelfSize += verbosePayloadArgument.getSize()
                    }
                } catch (e: Exception) {
                    println("$e SH: $standardHeader; EH: $extendedHeader; PL: $arguments")
                }

//                if (payloadSize != payloadSelfSize) {
//                    println("$payloadSize != $payloadSelfSize")
//                }
                i += payloadSize
                payload = VerbosePayload(arguments)

            } else if (extendedHeader.messageInfo.messageType == MessageInfo.MESSAGE_TYPE.DLT_TYPE_CONTROL) {
                val payloadSize =
                    standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                val messageId: UByte = bytes[i].toUByte()
                val payloadOffset: Int = ControlMessagePayload.CONTROL_MESSAGE_ID_SIZE_BYTES
                payload = ControlMessagePayload(
                    messageId, bytes.sliceArray(i + payloadOffset..<i + payloadSize)
                )

            } else {
                val payloadSize =
                    standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                val messageId: UInt = bytes.readInt(i, Endian.LITTLE).toUInt()
                val payloadOffset: Int = NonVerbosePayload.MESSAGE_ID_SIZE_BYTES
                payload = NonVerbosePayload(
                    messageId, bytes.sliceArray(i + payloadOffset..<i + payloadSize)
                )
            }
        }
        if (DEBUG_LOG && shouldLog) {
            println("")
        }

        return DLTMessage(timeStampNano, ecuId, standardHeader, extendedHeader, payload, i - offset)
    }

    private fun parseStandardHeader(shouldLog: Boolean, bytes: ByteArray, i: Int): StandardHeader {
        if (DEBUG_LOG && shouldLog) {
            println("StandardHeader.parse")
        }
        var p = i
        val headerType = parseStandardHeaderType(shouldLog, bytes[p]); p += 1
        val messageCounter = bytes[p].toUByte(); p += 1
        val length = bytes.readUShort(p, STANDARD_HEADER_ENDIAN); p += 2
        val ecuId =
            if (headerType.withEcuId) bytes.readString(p, 4).replace("\u0000", "") else null; p += 4
        val sessionId =
            if (headerType.withSessionId) bytes.readInt(p, STANDARD_HEADER_ENDIAN) else null; p += 4
        val timeStamp = if (headerType.withTimestamp) bytes.readInt(p, STANDARD_HEADER_ENDIAN)
            .toUInt() else null; p += 4

        if (DEBUG_LOG && shouldLog) {
            println(
                "   messageCounter: $messageCounter; length: $length: ecuId: '$ecuId', sessionId: $sessionId; timeStamp: ${
                    if (timeStamp != null) simpleDateFormat.format(timeStamp.toLong() / 10000) else "null"
                }"
            )
        }

        return StandardHeader(
            headerType, messageCounter, length, ecuId, sessionId, timeStamp
        )
    }

    private fun parseStandardHeaderType(shouldLog: Boolean, byte: Byte): StandardHeader.HeaderType {
        val useExtendedHeader = byte.isBitSet(0)
        val payloadBigEndian = byte.isBitSet(1)
        val withEcuId = byte.isBitSet(2)
        val withSessionId = byte.isBitSet(3)
        val withTimestamp = byte.isBitSet(4)
        val versionNumber = (byte.toInt() shr 5).toByte()

        if (DEBUG_LOG && shouldLog) {
            println(
                "   HeaderType.parse: " + "${byte.toHex()} (${
                    byte.toString(2).padStart(8, '0')
                }) " + "extendedHeader: $useExtendedHeader, payloadBigEndian: $payloadBigEndian, withEcuId: $withEcuId, withSessionId: $withSessionId, withTimestamp: $withTimestamp, versionNumber: $versionNumber)"
            )
        }

        return StandardHeader.HeaderType(
            byte,
            useExtendedHeader,
            payloadBigEndian,
            withEcuId,
            withSessionId,
            withTimestamp,
            versionNumber
        )
    }

    private fun parseExtendedHeader(shouldLog: Boolean, bytes: ByteArray, i: Int): ExtendedHeader {
        if (DEBUG_LOG && shouldLog) {
            println("ExtendedHeader.parse:")
        }
        var p = i
        val messageInfo = parseMessageInfo(bytes[p]); p += 1
        val argumentsCount = bytes[p].toUByte(); p += 1
        if (argumentsCount < 0U) {
            throw Exception("Negative arguments count $argumentsCount i: $i")
        }
        val applicationId = bytes.readString(p, 4); p += 4
        val contextId = bytes.readString(p, 4); p += 4

        if (DEBUG_LOG && shouldLog) {
            println(
                "   messageInfo: $messageInfo, argumentsCount: $argumentsCount, applicationId: $applicationId, contextId: $contextId"
            )
        }

        return ExtendedHeader(
            messageInfo, argumentsCount,
            // todo: check performance, could be done faster when reading bytes
            applicationId.replace("\u0000", ""), contextId.replace("\u0000", "")
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
        byte: Byte, messageType: MessageInfo.MESSAGE_TYPE
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


    fun parseVerbosePayload(
        shouldLog: Boolean, j: Int, bytes: ByteArray, i: Int, payloadEndian: Endian
    ): VerbosePayload.Argument {
        if (DEBUG_LOG && shouldLog) {
            println(
                "   $j: Argument.parse ($payloadEndian):  ${bytes.sliceArray(i..i + 20).toHex()}"
            )
        }

        val typeInfoInt = bytes.readInt(i, payloadEndian)
        val typeInfo = parseVerbosePayloadTypeInfo(shouldLog, typeInfoInt, payloadEndian)
        if (DEBUG_LOG && shouldLog) {
            println("       typeInfo: $typeInfo")
        }

        var payloadSize: Int
        var additionalSize = 0
        if (typeInfo.typeString) {
            payloadSize = bytes.readUShort(i + 4, payloadEndian).toInt()
            additionalSize =
                2 // PRS_Dlt_00156 - 16-bit unsigned integer specifies the length of the string
        } else if (typeInfo.typeRaw) {
            payloadSize = bytes.readUShort(i + 4, payloadEndian).toInt()
            additionalSize =
                2 // PRS_Dlt_00160 - 16-bit unsigned integer shall specify the length of the raw data in byte
        } else if (typeInfo.typeUnsigned || typeInfo.typeSigned) {
            payloadSize = typeInfo.typeLengthBits / 8
        } else if (typeInfo.typeBool) {
            payloadSize = 1
        } else if (typeInfo.typeFloat) {
            payloadSize = typeInfo.typeLengthBits / 8
        } else {
//            throw IllegalStateException("Can't parse ${typeInfo}")
            if (DEBUG_LOG) {
                println(
                    "Warning! Unsupported Payload type at offset: $i -> $typeInfo (${
                        typeInfoInt.toHex(
                            4
                        )
                    })"
                )
            }
            payloadSize = typeInfo.typeLengthBits / 8
        }

        // Sanity check to fix infinite reading
        if (payloadSize <= 0) {
            payloadSize = 1
        }

        val toIndex = i + 4 + additionalSize + payloadSize - 1
        val payload =
            bytes.sliceArray(i + 4 + additionalSize..if (toIndex < bytes.size) toIndex else bytes.size - 1)

        val argument = VerbosePayload.Argument(
            typeInfoInt, typeInfo, additionalSize, payloadSize, payload
        )

        if (DEBUG_LOG && shouldLog) {
            println(
                "       payload size: $payloadSize, content: ${argument.getPayloadAsText()}"
            )
        }

        return argument
    }

    fun parseVerbosePayloadTypeInfo(
        shouldLog: Boolean, typeInfoInt: Int, payloadEndian: Endian
    ): VerbosePayload.TypeInfo {
        val typeLengthBits = when (typeInfoInt and 0b1111) {
            0b0001 -> 8
            0b0010 -> 16
            0b0011 -> 32
            0b0100 -> 64
            0b0101 -> 128
            else -> 0
        }

        val stringCoding = when (typeInfoInt.shr(15) and STRING_CODING_MASK) {
            0 -> VerbosePayload.TypeInfo.STRING_CODING.ASCII
            1 -> VerbosePayload.TypeInfo.STRING_CODING.UTF8
            else -> VerbosePayload.TypeInfo.STRING_CODING.RESERVED
        }

        if (DEBUG_LOG && shouldLog) {
            println("   typeInfoInt: 0x${typeInfoInt.toHex(4)} (${typeInfoInt.toBinary(32)}b)")
            println("   typeLengthBits: $typeLengthBits (${(typeInfoInt and 0b1111).toBinary(32)}b)")
            println(
                "   stringCoding: $stringCoding (${
                    (typeInfoInt.shr(15) and STRING_CODING_MASK).toBinary(32)
                }b)"
            )
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