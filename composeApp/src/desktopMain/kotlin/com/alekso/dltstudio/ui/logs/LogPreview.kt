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
import com.alekso.dltparser.dlt.NonVerbosePayload
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltparser.dlt.VerbosePayload
import com.alekso.dltparser.toHex

@Composable
fun LogPreview(modifier: Modifier, dltMessage: DLTMessage?) {
    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)
    SelectionContainer(modifier = modifier) {
        Divider()
        Column(modifier = Modifier.then(Modifier.verticalScroll(rememberScrollState()))) {
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
                    when (val payload = dltMessage.payload) {
                        is VerbosePayload -> {
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
                                            .background(color = Color(250, 250, 250)),
                                        text = "$index"
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

                        is NonVerbosePayload -> {
                            Text(
                                modifier = paddingModifier,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                text = payload.asText()
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
fun PreviewLogPreview() {
    val dltMessage = SampleData.getSampleDltMessages(1)[0]
    LogPreview(Modifier.width(200.dp), dltMessage = dltMessage)
}
