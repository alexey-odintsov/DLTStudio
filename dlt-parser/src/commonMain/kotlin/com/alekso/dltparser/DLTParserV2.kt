package com.alekso.dltparser

import com.alekso.datautils.Endian
import com.alekso.datautils.isBitSet
import com.alekso.datautils.toHex
import com.alekso.dltmessage.BinaryDLTMessage
import com.alekso.dltmessage.ControlMessagePayload
import com.alekso.dltmessage.DLTMessage
import com.alekso.dltmessage.Payload
import com.alekso.dltmessage.PayloadStorageType
import com.alekso.dltmessage.PlainDLTMessage
import com.alekso.dltmessage.StructuredDLTMessage
import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltmessage.extendedheader.MessageInfo
import com.alekso.dltmessage.extendedheader.MessageType
import com.alekso.dltmessage.extendedheader.MessageTypeInfo
import com.alekso.dltmessage.nonverbosepayload.NonVerbosePayload
import com.alekso.dltmessage.standardheader.HeaderType
import com.alekso.dltmessage.standardheader.StandardHeader
import com.alekso.dltmessage.verbosepayload.VerbosePayload
import com.alekso.dltparser.DLTParser.Companion.DEBUG_LOG
import com.alekso.dltparser.DLTParser.Companion.DLT_HEADER_SIZE_BYTES
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_01
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_D
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_L
import com.alekso.dltparser.DLTParser.Companion.SIGNATURE_T
import com.alekso.dltparser.DLTParser.Companion.simpleDateFormat
import com.alekso.logger.Log
import java.io.EOFException
import java.io.File


private const val PROGRESS_UPDATE_DEBOUNCE_MS = 30

class DLTParserV2() : DLTParser {

    init {
        Log.d("Init parser ${this.javaClass.simpleName}")
    }

    /**
     * https://www.autosar.org/fileadmin/standards/R20-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf
     * https://www.autosar.org/fileadmin/standards/R22-11/FO/AUTOSAR_PRS_LogAndTraceProtocol.pdf - Header Type for protocol version "2"
     */
    override suspend fun read(
        files: List<File>,
        payloadStorageType: PayloadStorageType,
        progressCallback: (Float) -> Unit,
    ): List<DLTMessage> {
        progressCallback.invoke(0f)
        val messages = mutableListOf<DLTMessage>()
        var skippedBytes = 0
        val totalSize = files.sumOf { it.length() }
        var bytesRead: Long = 0
        val startMs = System.currentTimeMillis()

        // todo: What to do with non-DLT files? silently skip?
        Log.d("Parsing using payloadType:'$payloadStorageType' ${files.size} file(s) with size: $totalSize bytes")
        files.forEach { file ->
            val fileSize = file.length()
            Log.d("Parsing '$file'")
            ParserInputStream(file.inputStream().buffered(64 * 1024 * 1024)).use { stream ->
                var i = 0L
                var bufByte: Byte

                var prevTs = System.currentTimeMillis()

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

                        // DLT signature was matched, now we can try to parse DLT message
                        val (dltMessage, len) = parseDLTMessage(stream, i, payloadStorageType)
                        messages.add(dltMessage)
                        i += len
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
        return messages.sortedBy { it.timeStampUs }
    }

    fun parseDLTMessage(
        stream: ParserInputStream,
        offset: Long,
        payloadStorageType: PayloadStorageType,
    ): Pair<DLTMessage, Int> {
        var i = offset
        val timeStampSec = stream.readIntLittle()
        val microSeconds = stream.readIntLittle()
        val timeStampUs = timeStampSec * 1000000L + microSeconds
        val ecuId = stream.readString(4) // don't remove it!
        i += DLT_HEADER_SIZE_BYTES

        val standardHeader = parseStandardHeader(stream)
        i += standardHeader.getSize()

        var extendedHeader: ExtendedHeader? = null
        if (standardHeader.headerType.useExtendedHeader) {
            extendedHeader = parseExtendedHeader(stream)
            i += extendedHeader.getSize()
        }

        val payloadEndian =
            if (standardHeader.headerType.payloadBigEndian) Endian.BIG else Endian.LITTLE

        val dltMessage = when (payloadStorageType) {
            PayloadStorageType.Structured -> {
                var payload: Payload? = null
                if (extendedHeader != null) {
                    val payloadSize =
                        standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                    i += payloadSize
                    payload =
                        parseStructuredPayload(stream, payloadSize, extendedHeader, payloadEndian)
                }
                StructuredDLTMessage(timeStampUs, standardHeader, extendedHeader, payload)
            }

            PayloadStorageType.Plain -> {
                var payload: String? = null
                if (extendedHeader != null) {
                    val payloadSize =
                        standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                    i += payloadSize
                    payload = parseStructuredPayload(
                        stream,
                        payloadSize,
                        extendedHeader,
                        payloadEndian
                    )?.asText()
//                    if (payload.endsWith("\n")) {
                    payload?.removeSuffix("\n")
//                    }
                }
                PlainDLTMessage(
                    timeStampUs = timeStampUs,
                    standardHeader = standardHeader,
                    extendedHeader = extendedHeader,
                    payload = payload
                )
            }

            PayloadStorageType.Binary -> {
                var payload: ByteArray? = null
                if (extendedHeader != null) {
                    val payloadSize =
                        standardHeader.length.toInt() - standardHeader.getSize() - extendedHeader.getSize()
                    if (payloadSize <= 0) {
                        Log.e("Wrong payload size $payloadSize for offset: $offset headers: $standardHeader $extendedHeader")
                        i += 1
                    } else {
                        i += payloadSize
                        payload = parseBinaryPayload(stream, payloadSize)
                    }
                }
                BinaryDLTMessage(
                    timeStampUs = timeStampUs,
                    standardHeader = standardHeader,
                    extendedHeader = extendedHeader,
                    payload = payload,
                )
            }
        }
        return Pair(dltMessage, (i - offset).toInt())
    }

    private fun parseBinaryPayload(stream: ParserInputStream, payloadSize: Int): ByteArray {
        return stream.readNBytes(payloadSize)
    }

    private fun parseStructuredPayload(
        stream: ParserInputStream,
        payloadSize: Int,
        extendedHeader: ExtendedHeader?,
        payloadEndian: Endian,
    ): Payload? {
        var payload: Payload? = null
        if (extendedHeader?.messageInfo?.verbose == true) {
            val rawPayload = stream.readNBytes(payloadSize)
            payload = VerbosePayload.parse(
                rawPayload,
                extendedHeader.argumentsCount.toInt(),
                payloadEndian
            )

        } else if (extendedHeader?.messageInfo?.messageType == MessageType.DLT_TYPE_CONTROL) {
            val messageId: Int = if (payloadEndian == Endian.BIG) {
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
                payload = ControlMessagePayload(
                    messageId,
                    response,
                    stream.readNBytes(payloadSize - payloadOffset)
                )
            }

        } else {
            val messageId: UInt = stream.readIntLittle().toUInt()
            val payloadOffset: Int = NonVerbosePayload.MESSAGE_ID_SIZE_BYTES

            if ((payloadSize - payloadOffset) > 0) {
                payload = NonVerbosePayload(
                    messageId,
                    stream.readNBytes(payloadSize - payloadOffset)
                )
            }
        }

        return payload
    }

    private fun parseStandardHeader(
        stream: ParserInputStream,
    ): StandardHeader {
        val headerType = parseStandardHeaderType(stream.readByte())
        val messageCounter = stream.readUnsignedByte().toUByte()
        val length = stream.readUnsignedShort().toUShort()
        val ecuId = if (headerType.withEcuId) stream.readString(4) else null
        val sessionId = if (headerType.withSessionId) stream.readInt() else null
        val timeStamp = if (headerType.withTimestamp) stream.readInt().toUInt() else null

        if (DEBUG_LOG) {
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

    private fun parseStandardHeaderType(byte: Byte): HeaderType {
        val useExtendedHeader = byte.isBitSet(0)
        val payloadBigEndian = byte.isBitSet(1)
        val withEcuId = byte.isBitSet(2)
        val withSessionId = byte.isBitSet(3)
        val withTimestamp = byte.isBitSet(4)
        val versionNumber = (byte.toInt() shr 5).toByte()

        if (DEBUG_LOG) {
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
        stream: ParserInputStream,
    ): ExtendedHeader {
        if (DEBUG_LOG) {
            println("ExtendedHeader.parse:")
        }
        val messageInfo = parseMessageInfo(stream.readByte())
        val argumentsCount = stream.readUnsignedByte().toUByte()
        val applicationId = stream.readString(4)
        val contextId = stream.readString(4)

        if (DEBUG_LOG) {
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

}