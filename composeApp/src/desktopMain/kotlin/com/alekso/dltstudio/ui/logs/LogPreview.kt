package com.alekso.dltstudio.ui.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.ExtendedHeader
import com.alekso.dltparser.dlt.MessageInfo
import com.alekso.dltparser.dlt.StandardHeader
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltparser.toHex

@Composable
fun LogPreview(modifier: Modifier, dltMessage: DLTMessage?) {
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)
    SelectionContainer {

        Divider()
        Column(modifier = modifier.then(Modifier.verticalScroll(rememberScrollState()))) {
            if (dltMessage != null) {
                Text(
                    modifier = paddingModifier,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight(600),
                    fontSize = 11.sp,
                    text = "Standard header:"
                )
                MonoText(
                    modifier = paddingModifier,
                    text = "Header Type: ${dltMessage.standardHeader.headerType.originalByte.toHex()} (${
                        dltMessage.standardHeader.headerType.originalByte.toString(
                            2
                        ).padStart(8, '0')
                    })"
                )
                MonoText(
                    modifier = paddingModifier,
                    text = "${dltMessage.standardHeader.headerType}"
                )
                MonoText(
                    modifier = paddingModifier,
                    text = "${dltMessage.standardHeader}"
                )
                Divider()
                Text(
                    modifier = paddingModifier,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight(600),
                    fontSize = 11.sp,
                    text = "Extended header:"
                )
                MonoText(
                    modifier = paddingModifier,
                    text = "${dltMessage.extendedHeader}"
                )
                Divider()
                Text(
                    modifier = paddingModifier,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight(600),
                    fontSize = 11.sp,
                    text = "Payload:"
                )
                if (dltMessage.payload != null) {
                    val payload = dltMessage.payload as VerbosePayload
                    Text(
                        modifier = paddingModifier,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        text = "Arguments found: ${payload.arguments.size}"
                    )

                    Row(modifier = paddingModifier) {
                        MonoText(
                            modifier = Modifier.width(20.dp).padding(end = 1.dp)
                                .background(color = Color(250, 250, 250)), text = "#"
                        )
                        MonoText(
                            modifier = Modifier.width(120.dp).padding(end = 1.dp)
                                .background(color = Color(250, 250, 250)), text = "type"
                        )
                        MonoText(
                            modifier = Modifier.width(40.dp).padding(end = 1.dp)
                                .background(color = Color(250, 250, 250)), text = "size"
                        )
                        MonoText(
                            modifier = Modifier.weight(1f).padding(end = 1.dp)
                                .background(color = Color(250, 250, 250)), text = "payload"
                        )
                    }
                    payload.arguments.forEachIndexed { index, it ->
                        Row(modifier = paddingModifier) {
                            MonoText(
                                modifier = Modifier.width(20.dp).padding(end = 1.dp)
                                    .background(color = Color(250, 250, 250)), text = "$index"
                            )
                            MonoText(
                                modifier = Modifier.width(120.dp).padding(end = 1.dp)
                                    .background(color = Color(250, 250, 250)),
                                text = it.typeInfo.getTypeString()
                            )
                            MonoText(
                                modifier = Modifier.width(40.dp).padding(end = 1.dp)
                                    .background(color = Color(250, 250, 250)),
                                text = "${it.payloadSize}"
                            )
                            MonoText(
                                modifier = Modifier.weight(1f).padding(end = 1.dp)
                                    .background(color = Color(250, 250, 250)),
                                text = it.getPayloadAsText()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonoText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.then(Modifier),
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        text = text
    )
}

@Preview
@Composable
fun previewLogPreview() {
    val dltMessage = DLTMessage(
        21142234, 243243, "MGUA",
        StandardHeader(
            StandardHeader.HeaderType(0.toByte(), true, true, true, true, true, 1),
            10.toUByte(), 10U, "MGUA", 443, 332422U
        ),
        ExtendedHeader(
            MessageInfo(
                30.toByte(),
                true,
                MessageInfo.MESSAGE_TYPE.DLT_TYPE_APP_TRACE,
                MessageInfo.MESSAGE_TYPE_INFO.DLT_LOG_INFO
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
                        VerbosePayload.TypeInfo.STRING_CODING.UTF8
                    ), 12, 10, "TEST MESSAGE".toByteArray()
                )
            )
        ),
        122
    )
    LogPreview(Modifier, dltMessage = dltMessage)
}
