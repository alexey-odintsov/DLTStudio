package com.alekso.dltstudio.plugins.dltdetailedview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.datautils.toBinary
import com.alekso.datautils.toHex
import com.alekso.dltmessage.SampleData
import com.alekso.dltmessage.extendedheader.ExtendedHeader
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.uicomponents.table.TableTextCell

@Composable
fun DLTDetailedInfoView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    messageIndex: Int,
) {
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)
    val mergedColModifier = Modifier.padding(start = 4.dp, end = 4.dp).fillMaxWidth()
    val firstColModifier = Modifier.padding(start = 4.dp, end = 4.dp).width(150.dp)

    SelectionContainer {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            logMessage?.dltMessage?.let {

                TableTextCell(
                    mergedColModifier,
                    text = "DLT Message #$messageIndex:",
                    isHeader = true
                )
                Row {
                    TableTextCell(firstColModifier, text = "Timestamp")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.timeStampUs}",
                    )
                }

                TableTextCell(mergedColModifier, text = "Standard header:", isHeader = true)
                Row {
                    TableTextCell(firstColModifier, text = "Header Type")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "0x${it.standardHeader.headerType.originalByte.toHex()} " +
                                "(${it.standardHeader.headerType.originalByte.toBinary(8)}b)",
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "  Extender header")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.standardHeader.headerType.useExtendedHeader}"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "  Payload Endian")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = if (it.standardHeader.headerType.payloadBigEndian) "BIG" else "LITTLE"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "  ECU present")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.standardHeader.headerType.withEcuId}"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "  Session present")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.standardHeader.headerType.withSessionId}"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "  Timestamp present")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.standardHeader.headerType.withTimestamp}"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "  Version number")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "0x${it.standardHeader.headerType.versionNumber.toHex()}"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "Message counter")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.standardHeader.messageCounter}"
                    )
                }

                Row {
                    TableTextCell(firstColModifier, text = "Length")
                    TableTextCell(
                        paddingModifier.weight(1f),
                        text = "${it.standardHeader.length}"
                    )
                }

                if (it.standardHeader.headerType.withEcuId) {
                    Row {
                        TableTextCell(firstColModifier, text = "ECU Id")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "'${it.standardHeader.ecuId}'"
                        )
                    }
                }
                if (it.standardHeader.headerType.withSessionId) {
                    Row {
                        TableTextCell(firstColModifier, text = "Session Id")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "${it.standardHeader.sessionId}"
                        )
                    }
                }

                if (it.standardHeader.headerType.withTimestamp) {
                    Row {
                        TableTextCell(firstColModifier, text = "Timestamp")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "${it.standardHeader.timeStamp}"
                        )
                    }
                }

                if (it.extendedHeader != null) {
                    val extendedHeader: ExtendedHeader = it.extendedHeader!!
                    TableTextCell(mergedColModifier, text = "Extended header:", isHeader = true)
                    Row {
                        TableTextCell(firstColModifier, text = "  Message info")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "0x${extendedHeader.messageInfo.originalByte.toHex()} " +
                                    "(${extendedHeader.messageInfo.originalByte.toBinary(8)}b)"
                        )
                    }
                    Row {
                        TableTextCell(firstColModifier, text = "  Verbose")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "${extendedHeader.messageInfo.verbose}"
                        )
                    }
                    Row {
                        TableTextCell(firstColModifier, text = "  Message type")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "${extendedHeader.messageInfo.messageType}"
                        )
                    }
                    Row {
                        TableTextCell(firstColModifier, text = "  Message type info")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "${extendedHeader.messageInfo.messageTypeInfo}"
                        )
                    }
                    Row {
                        TableTextCell(firstColModifier, text = "Arguments count")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "${extendedHeader.argumentsCount}"
                        )
                    }
                    Row {
                        TableTextCell(firstColModifier, text = "Application Id")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "'${extendedHeader.applicationId}'"
                        )
                    }
                    Row {
                        TableTextCell(firstColModifier, text = "Context Id")
                        TableTextCell(
                            paddingModifier.weight(1f),
                            text = "'${extendedHeader.contextId}'"
                        )
                    }
                }

                TableTextCell(mergedColModifier, text = "Payload:", isHeader = true)
                TableTextCell(mergedColModifier, text = it.payloadText())
            }
        }
    }
}

@Preview
@Composable
fun PreviewDLTDetailedInfoView() {
    DLTDetailedInfoView(
        logMessage = LogMessage(SampleData.create(payloadText = "Test message")),
        messageIndex = 1
    )
}