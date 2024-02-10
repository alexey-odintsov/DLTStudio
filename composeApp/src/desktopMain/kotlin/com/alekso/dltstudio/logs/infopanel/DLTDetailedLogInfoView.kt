package com.alekso.dltstudio.logs.infopanel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.NonVerbosePayload
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltparser.toBinary
import com.alekso.dltparser.toHex

@Composable
fun DLTDetailedInfoView(
    modifier: Modifier = Modifier,
    dltMessage: DLTMessage?,
    messageIndex: Int
) {
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    SelectionContainer {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            if (dltMessage != null) {
                val parameterRowWidth = 150
                Header(
                    modifier = paddingModifier,
                    text = "DLT Message #$messageIndex:"
                )
                TableRow(
                    parameterRowWidth, "Timestamp",
                    "${dltMessage.timeStampNano}"
                )
                TableRow(
                    parameterRowWidth, "ECU Id",
                    "'${dltMessage.ecuId}'"
                )

                Header(
                    modifier = paddingModifier,
                    text = "Standard header:"
                )

                TableRow(
                    parameterRowWidth, "Header Type",
                    "0x${dltMessage.standardHeader.headerType.originalByte.toHex()} " +
                            "(${dltMessage.standardHeader.headerType.originalByte.toBinary(8)}b)"
                )
                TableRow(
                    parameterRowWidth, "  Extender header",
                    "${dltMessage.standardHeader.headerType.useExtendedHeader}"
                )
                TableRow(
                    parameterRowWidth, "  Payload Endian",
                    if (dltMessage.standardHeader.headerType.payloadBigEndian) "BIG" else "LITTLE"
                )
                TableRow(
                    parameterRowWidth, "  ECU present",
                    "${dltMessage.standardHeader.headerType.withEcuId}"
                )
                TableRow(
                    parameterRowWidth, "  Session present",
                    "${dltMessage.standardHeader.headerType.withSessionId}"
                )
                TableRow(
                    parameterRowWidth, "  Timestamp present",
                    "${dltMessage.standardHeader.headerType.withTimestamp}"
                )
                TableRow(
                    parameterRowWidth, "  Version number",
                    "0x${dltMessage.standardHeader.headerType.versionNumber.toHex()}"
                )

                TableRow(
                    parameterRowWidth, "Message counter",
                    "${dltMessage.standardHeader.messageCounter}"
                )
                TableRow(
                    parameterRowWidth, "Length",
                    "${dltMessage.standardHeader.length}"
                )
                if (dltMessage.standardHeader.headerType.withEcuId) {
                    TableRow(
                        parameterRowWidth, "ECU Id",
                        "'${dltMessage.standardHeader.ecuId}'"
                    )
                }
                if (dltMessage.standardHeader.headerType.withSessionId) {
                    TableRow(
                        parameterRowWidth, "Session Id",
                        "${dltMessage.standardHeader.sessionId}"
                    )
                }

                if (dltMessage.standardHeader.headerType.withTimestamp) {
                    TableRow(
                        parameterRowWidth, "Timestamp",
                        "${dltMessage.standardHeader.timeStamp}"
                    )
                }

                if (dltMessage.extendedHeader != null) {
                    val extendedHeader: ExtendedHeader = dltMessage.extendedHeader!!
                    Header(
                        modifier = paddingModifier,
                        text = "Extended header:"
                    )
                    TableRow(
                        parameterRowWidth, "  Message info",
                        "0x${extendedHeader.messageInfo.originalByte.toHex()} " +
                                "(${extendedHeader.messageInfo.originalByte.toBinary(8)}b)"
                    )
                    TableRow(
                        parameterRowWidth, "  Verbose",
                        "${extendedHeader.messageInfo.verbose}"
                    )
                    TableRow(
                        parameterRowWidth, "  Message type",
                        "${extendedHeader.messageInfo.messageType}"
                    )
                    TableRow(
                        parameterRowWidth, "  Message type info",
                        "${extendedHeader.messageInfo.messageTypeInfo}"
                    )
                    TableRow(
                        parameterRowWidth, "Arguments count",
                        "${extendedHeader.argumentsCount}"
                    )
                    TableRow(
                        parameterRowWidth, "Application Id",
                        "'${extendedHeader.applicationId}'"
                    )
                    TableRow(
                        parameterRowWidth, "Context Id",
                        "'${extendedHeader.contextId}'"
                    )

                }


                if (dltMessage.payload != null) {

                    when (val payload = dltMessage.payload) {
                        is VerbosePayload -> {
                            Header(
                                modifier = paddingModifier,
                                text = "Verbose payload (${payload.arguments.size} arguments):"
                            )

                            payload.arguments.forEachIndexed { index, it ->
                                TableRow(
                                    0, "",
                                    "#$index ${it.typeInfo.getTypeString()} ${it.payloadSize} bytes"
                                )
                                TableRow(0, "", it.getPayloadAsText())

                            }
                        }

                        is NonVerbosePayload -> {
                            Header(
                                modifier = paddingModifier,
                                text = "Non-Verbose payload:"
                            )
                            MonoText(
                                modifier = paddingModifier,
                                text = payload.asText()
                            )
                        }
                    }
                }
            }
        }
    }
}