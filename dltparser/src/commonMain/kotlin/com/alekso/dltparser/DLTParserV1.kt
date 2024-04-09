package com.alekso.dltparser

import com.alekso.dltparser.DLTParser.Companion.DEBUG_LOG
import com.alekso.dltparser.DLTParser.Companion.DLT_HEADER_SIZE_BYTES
import com.alekso.dltparser.DLTParser.Companion.MAX_BYTES_TO_READ_DEBUG
import com.alekso.dltparser.DLTParser.Companion.STANDARD_HEADER_ENDIAN
import com.alekso.dltparser.DLTParser.Companion.STRING_CODING_MASK
import com.alekso.dltparser.DLTParser.Companion.simpleDateFormat
import com.alekso.dltparser.dlt.ControlMessagePayload
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltparser.dlt.NonVerbosePayload
import com.alekso.dltparser.dlt.Payload
import com.alekso.dltparser.dlt.StandardHeader
import com.alekso.dltparser.dlt.VerbosePayload
import java.io.File


class DLTParserV1: DLTParser {

    override suspend fun read(
        progressCallback: (Float) -> Unit, files: List<File>
    ): List<DLTMessage> {
        progressCallback.invoke(0f)
        val messages = mutableListOf<DLTMessage>()
        var skippedBytes = 0
        val totalSize = files.sumOf { it.length() }
        var bytesRead: Long = 0
        val startMs = System.currentTimeMillis()

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
                        val dltMessage = parseDLTMessage(bytes, i, shouldLog)
                        messages.add(dltMessage)
                        i += dltMessage.sizeBytes // skip read bytes
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
        println("Parsing complete in ${(System.currentTimeMillis() - startMs) / 1000} sec. Parsed ${messages.size} messages; $bytesRead bytes read and $skippedBytes skipped bytes")
        return messages.sortedBy { it.timeStampNano }
    }

    fun parseDLTMessage(bytes: ByteArray, offset: Int, shouldLog: Boolean): DLTMessage {
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

            } else if (extendedHeader.messageInfo.messageType == MessageInfo.MessageType.DLT_TYPE_CONTROL) {
                val payloadSize =
                    standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                val messageId: Int = bytes.readInt(
                    i,
                    if (standardHeader.headerType.payloadBigEndian) Endian.BIG else Endian.LITTLE
                )
                var response: Int? = null
                var payloadOffset: Int = ControlMessagePayload.CONTROL_MESSAGE_ID_SIZE_BYTES
                if (extendedHeader.messageInfo.messageTypeInfo == MessageInfo.MessageTypeInfo.DLT_CONTROL_RESPONSE && (payloadSize - payloadOffset) > 0) {
                    response = bytes[i + payloadOffset].toInt()
                    payloadOffset += ControlMessagePayload.CONTROL_MESSAGE_RESPONSE_SIZE_BYTES
                }
                payload = ControlMessagePayload(
                    messageId,
                    response,
                    bytes.sliceArray(i + payloadOffset..<i + payloadSize)
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

        return DLTMessage(timeStampNano, ecuId, standardHeader, extendedHeader, payload?.asText() ?: "", i - offset)
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


    private fun parseMessageInfo(byte: Byte): MessageInfo {
        val messageType = MessageInfo.messageTypeInfoFromByte(byte)

        return MessageInfo(
            originalByte = byte,
            verbose = byte.isBitSet(0),
            messageType = messageType,
            messageTypeInfo = MessageInfo.messageTypeInfoFromByte(byte, messageType)
        )
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
            0 -> VerbosePayload.TypeInfo.StringCoding.ASCII
            1 -> VerbosePayload.TypeInfo.StringCoding.UTF8
            else -> VerbosePayload.TypeInfo.StringCoding.RESERVED
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