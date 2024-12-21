package com.alekso.dltparser

import com.alekso.dltparser.DLTParser.Companion.DEBUG_LOG
import com.alekso.dltparser.DLTParser.Companion.DLT_HEADER_SIZE_BYTES
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_01
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_D
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_L
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_T
import com.alekso.dltparser.DLTParser.Companion.STRING_CODING_MASK
import com.alekso.dltparser.DLTParser.Companion.simpleDateFormat
import com.alekso.dltparser.dlt.ControlMessagePayload
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.StringDLTMessage
import com.alekso.dltparser.dlt.extendedheader.ExtendedHeader
import com.alekso.dltparser.dlt.extendedheader.MessageInfo
import com.alekso.dltparser.dlt.extendedheader.MessageType
import com.alekso.dltparser.dlt.extendedheader.MessageTypeInfo
import com.alekso.dltparser.dlt.nonverbosepayload.NonVerbosePayload
import com.alekso.dltparser.dlt.standardheader.HeaderType
import com.alekso.dltparser.dlt.standardheader.StandardHeader
import com.alekso.dltparser.dlt.verbosepayload.Argument
import com.alekso.dltparser.dlt.verbosepayload.TypeInfo
import com.alekso.logger.Log
import java.io.EOFException
import java.io.File


private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class DLTParserV2 : DLTParser {

    /**
     * https://www.autosar.org/fileadmin/standards/R20-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf
     * https://www.autosar.org/fileadmin/standards/R22-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf - Header Type for protocol version "2"
     */
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
        Log.d("Total ${files.size} file(s) with size: $totalSize bytes")
        files.forEach { file ->
            val fileSize = file.length()
            Log.d("Parsing '$file'")
            ParserInputStream(file.inputStream().buffered(64 * 1024 * 1024)).use { stream ->
                var i = 0L
                var shouldLog: Boolean
                var bufByte: Byte

                var prevTs  = System.currentTimeMillis()

                while (i < fileSize) { // while (stream.available() > 4) is twice slow and unreliable in some implementations
                    try {
                        // Skip until 'DLT' signature found
                        bufByte = stream.readByte()
                        if (bufByte != SIGNATURE_D) {
                            i++; skippedBytes++
                            continue
                        }
                        bufByte = stream.readByte()
                        if (bufByte != SIGNATURE_L) {
                            i++; skippedBytes++
                            continue
                        }
                        bufByte = stream.readByte()
                        if (bufByte != SIGNATURE_T) {
                            i++; skippedBytes++
                            continue
                        }
                        bufByte = stream.readByte()
                        if (bufByte != SIGNATURE_01) {
                            i++; skippedBytes++
                            continue
                        }
                        shouldLog = false //logsReadCount == 3

                        // DLT signature was matched, now we can try to parse DLT message
                        val dltMessage = parseDLTMessage(stream, i, shouldLog)
                        messages.add(dltMessage)
                        i += dltMessage.sizeBytes
                    } catch (e: EOFException) {
                        i = fileSize
                    }
                    val nowTs = System.currentTimeMillis()
                    if (nowTs - prevTs > PROGRESS_UPDATE_DEBOUNCE_MS) {
                        prevTs = nowTs
                        progressCallback.invoke((bytesRead.toFloat() + i) / totalSize)
                    }
                }

                bytesRead += i
            }
        }
        progressCallback.invoke(1f)
        Log.d("Parsing complete in ${(System.currentTimeMillis() - startMs) / 1000} sec. Parsed ${messages.size} messages; $bytesRead bytes read and $skippedBytes skipped bytes")
        return messages.sortedBy { it.timeStampNano }
    }

    fun parseDLTMessage(
        stream: ParserInputStream,
        offset: Long,
        shouldLog: Boolean
    ): DLTMessage {
        var i = offset
        val timeStampSec = stream.readIntLittle()
        val timeStampUs = stream.readIntLittle()
        val timeStampNano = timeStampSec * 1000000L + timeStampUs
        val ecuId = stream.readString(4)
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
        val standardHeader = parseStandardHeader(shouldLog, stream)
        i += standardHeader.getSize()

        var extendedHeader: ExtendedHeader? = null
        if (standardHeader.headerType.useExtendedHeader) {
            extendedHeader = parseExtendedHeader(shouldLog, stream)
            i += extendedHeader.getSize()
        }

        val payload = StringBuilder()

        if (extendedHeader != null) {
            if (extendedHeader.messageInfo.verbose) {
                if (DEBUG_LOG && shouldLog) {
                    println(
                        "Payload.parse: ${extendedHeader.argumentsCount} payload arguments found"
                    )
                }
                val payloadSize =
                    (standardHeader.length.toLong() - standardHeader.getSize() - extendedHeader.getSize()).toInt()
                try {
                    for (argumentIndex in 0..<extendedHeader.argumentsCount.toInt()) {

                        val verbosePayloadArgument = parseVerbosePayload(
                            shouldLog,
                            stream,
                            if (standardHeader.headerType.payloadBigEndian) Endian.BIG else Endian.LITTLE
                        )
                        val argumentString = verbosePayloadArgument.getPayloadAsText()
                        if (payload.isNotEmpty() && !payload.endsWith(" ") && !argumentString.startsWith(" ")) {
                            payload.append(" ")
                        }
                        payload.append(argumentString)
                    }
                } catch (e: Exception) {
                    Log.w("$e SH: $standardHeader; EH: $extendedHeader;")
                }
                i += payloadSize

            } else if (extendedHeader.messageInfo.messageType == MessageType.DLT_TYPE_CONTROL) {
                val payloadSize =
                    standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                val messageId: Int = if (standardHeader.headerType.payloadBigEndian) {
                    stream.readInt()
                } else {
                    stream.readIntLittle()
                }
                var response: Int? = null
                var payloadOffset: Int = ControlMessagePayload.CONTROL_MESSAGE_ID_SIZE_BYTES
                if (extendedHeader.messageInfo.messageTypeInfo == MessageTypeInfo.DLT_CONTROL_RESPONSE && (payloadSize - payloadOffset) > 0) {
                    response = stream.readByte().toInt()
                    payloadOffset += ControlMessagePayload.CONTROL_MESSAGE_RESPONSE_SIZE_BYTES
                }
                if ((payloadSize - payloadOffset) > 0) {
                    payload.append(ControlMessagePayload(
                        messageId,
                        response,
                        stream.readNBytes(payloadSize - payloadOffset)
                    ).asText())
                }

            } else {
                val payloadSize =
                    standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                val messageId: UInt = stream.readIntLittle().toUInt()
                val payloadOffset: Int = NonVerbosePayload.MESSAGE_ID_SIZE_BYTES

                if ((payloadSize - payloadOffset) > 0) {
                    payload.append(NonVerbosePayload(messageId, stream.readNBytes(payloadSize - payloadOffset)).asText())
                }
            }
        }
//        if (DEBUG_LOG && shouldLog) {
//            println("")
//        }
        if (payload.endsWith("\n")) {
            payload.deleteCharAt(payload.length - 1)
        }

        return StringDLTMessage(timeStampNano, ecuId, standardHeader, extendedHeader,
            payload.toString().toByteArray(),
            (i - offset).toInt())
    }

    private fun parseStandardHeader(
        shouldLog: Boolean,
        stream: ParserInputStream,
    ): StandardHeader {
        if (DEBUG_LOG && shouldLog) {
            println("StandardHeader.parse")
        }

        val headerType = parseStandardHeaderType(shouldLog, stream.readByte())
        val messageCounter = stream.readUnsignedByte().toUByte()
        val length = stream.readUnsignedShort().toUShort()
        val ecuId = if (headerType.withEcuId) stream.readString(4) else null
        val sessionId = if (headerType.withSessionId) stream.readInt() else null
        val timeStamp = if (headerType.withTimestamp) stream.readInt().toUInt() else null

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

    private fun parseStandardHeaderType(shouldLog: Boolean, byte: Byte): HeaderType {
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

        return HeaderType(
            byte,
            useExtendedHeader,
            payloadBigEndian,
            withEcuId,
            withSessionId,
            withTimestamp,
            versionNumber
        )
    }

    private fun parseExtendedHeader(
        shouldLog: Boolean,
        stream: ParserInputStream,
    ): ExtendedHeader {
        if (DEBUG_LOG && shouldLog) {
            println("ExtendedHeader.parse:")
        }
        val messageInfo = parseMessageInfo(stream.readByte())
        val argumentsCount = stream.readUnsignedByte().toUByte()
        val applicationId = stream.readString(4)
        val contextId = stream.readString(4)

        if (DEBUG_LOG && shouldLog) {
            println(
                "   messageInfo: $messageInfo, argumentsCount: $argumentsCount, applicationId: $applicationId, contextId: $contextId"
            )
        }

        return ExtendedHeader(
            messageInfo, argumentsCount,
            applicationId, contextId
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

    private fun parseVerbosePayload(
        shouldLog: Boolean, stream: ParserInputStream, payloadEndian: Endian
    ): Argument {
        val typeInfoInt = if (payloadEndian == Endian.LITTLE) {
            stream.readIntLittle()
        } else {
            stream.readInt()
        }

        val typeInfo = parseVerbosePayloadTypeInfo(shouldLog, typeInfoInt, payloadEndian)
        if (DEBUG_LOG && shouldLog) {
            println("       typeInfo: $typeInfo")
        }

        var payloadSize: Int
        var additionalSize = 0
        if (typeInfo.typeString) {
            payloadSize = if (payloadEndian == Endian.BIG) {
                stream.readUnsignedShort()
            } else {
                stream.readUnsignedShortLittle()
            }
            additionalSize =
                2 // PRS_Dlt_00156 - 16-bit unsigned integer specifies the length of the string
        } else if (typeInfo.typeRaw) {
            payloadSize = if (payloadEndian == Endian.BIG) {
                stream.readUnsignedShort()
            } else {
                stream.readUnsignedShortLittle()
            }
            additionalSize =
                2 // PRS_Dlt_00160 - 16-bit unsigned integer shall specify the length of the raw data in byte
        } else if (typeInfo.typeUnsigned || typeInfo.typeSigned) {
            payloadSize = typeInfo.typeLengthBits / 8
        } else if (typeInfo.typeBool) {
            payloadSize = 1
        } else if (typeInfo.typeFloat) {
            payloadSize = typeInfo.typeLengthBits / 8
        } else {
            Log.e("Can't parse payload for typeInfo: ${typeInfo}")
            payloadSize = typeInfo.typeLengthBits / 8
        }

        // Sanity check to fix infinite reading
        if (payloadSize <= 0) {
            payloadSize = 1
        }

        val payload = stream.readNBytes(payloadSize)

        val argument = Argument(
            typeInfoInt, typeInfo, additionalSize, payloadSize, payload
        )

        if (DEBUG_LOG && shouldLog) {
            println(
                "       payload size: $payloadSize, content: ${argument.getPayloadAsText()}"
            )
        }

        return argument
    }

    private fun parseVerbosePayloadTypeInfo(
        shouldLog: Boolean, typeInfoInt: Int, payloadEndian: Endian
    ): TypeInfo {
        val typeLengthBits = when (typeInfoInt and 0b1111) {
            0b0001 -> 8
            0b0010 -> 16
            0b0011 -> 32
            0b0100 -> 64
            0b0101 -> 128
            else -> 0
        }

        val stringCoding = when (typeInfoInt.shr(15) and STRING_CODING_MASK) {
            0 -> TypeInfo.StringCoding.ASCII
            1 -> TypeInfo.StringCoding.UTF8
            else -> TypeInfo.StringCoding.RESERVED
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

        return TypeInfo(
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