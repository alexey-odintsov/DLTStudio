package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltparser.dlt.DLTMessage
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltparser.dlt.standardheader.HeaderType
import com.alekso.dltparser.dlt.standardheader.StandardHeader
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.colors.ColorPalette
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomEditText
import kotlin.math.min

@Composable
fun DLTSimplifiedInfoView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    messageIndex: Int,
    onCommentUpdated: (LogMessage, String?) -> Unit = { _, _ -> },
) {
    if (logMessage == null) return

    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)
    var comment by remember(key1 = logMessage) { mutableStateOf(logMessage.comment) }

    SelectionContainer {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            logMessage.dltMessage.let {
                Header(
                    modifier = paddingModifier,
                    text = "DLT Message #$messageIndex:"
                )
                val headerText = "${TimeFormatter.formatDateTime(it.timeStampNano)} " +
                        "${it.extendedHeader?.applicationId} " +
                        "${it.extendedHeader?.contextId} "
                TableRow(0, "", headerText)
                TableRow(0, "", it.payload)

                Header(
                    modifier = paddingModifier.padding(top = 8.dp),
                    text = "Comment:"
                )
                Row {
                    CustomEditText(
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                            .padding(top = 4.dp)
                            .align(Alignment.Top),
                        singleLine = false,
                        value = comment ?: "",
                        onValueChange = {
                            comment = it
                        }
                    )
                }
                CustomButton(
                    modifier = Modifier.padding(start = 6.dp),
                    onClick = {
                        onCommentUpdated(logMessage, if (comment.isNullOrEmpty()) null else comment)
                    }) {
                    Text(text = if (logMessage.comment != null) "Update" else "Add")
                }

                val deviceViews = DeviceView.parse(logMessage.dltMessage.payload)
                if (deviceViews.isNullOrEmpty().not()) {
                    DevicePreview(
                        modifier = Modifier.fillMaxSize(),
                        deviceSize = Size(2880f, 960f),
                        deviceViews
                    )
                }
            }
        }
    }
}

@Composable
fun DevicePreview(
    modifier: Modifier,
    deviceSize: Size,
    deviceViews: List<DeviceView>?,
) {
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier) {
        Text(
            text = "Device preview",
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight(600),
        )

        deviceViews?.forEachIndexed { i, view ->
            Text(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                text = "$i: ${view.rect} ${view.id}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
            )
        }

        Canvas(modifier = Modifier.fillMaxSize(1f).background(Color.White).padding(12.dp)) {
            if (size.width < 10 || size.height < 10) return@Canvas

            val rectStyle = Stroke(width = 2.dp.toPx())

            val scale: Float =
                min(size.width / deviceSize.width, size.height / deviceSize.height)

            val w = scale * deviceSize.width
            val h = scale * deviceSize.height
            val x: Float = (size.width - w) * 0.5f
            val y: Float = (size.height - h) * 0.5f
            withTransform({
                scale(
                    scaleX = scale, scaleY = scale, pivot = Offset(x, y)
                )
//            inset(padding, padding, padding, padding) // Preview is broken and padding is wrong
            }) {
                drawRect(
                    color = Color.LightGray,
                    Offset(0f, 0f),
                    size = deviceSize,
                    style = Fill
                )
                drawRect(
                    color = Color.Gray,
                    Offset(0f, 0f),
                    size = deviceSize,
                    style = rectStyle
                )

                deviceViews?.forEachIndexed { i, view ->
                    val color = ColorPalette.getColor(i)
                    drawRect(
                        color,
                        Offset(view.rect.left, view.rect.top),
                        size = view.rect.size,
                        style = rectStyle
                    )
                    drawText(
                        size = Size(200f, 200f),
                        topLeft = Offset(
                            view.rect.left + view.rect.size.width / 2f,
                            view.rect.top + view.rect.size.height / 2f
                        ),
                        textMeasurer = textMeasurer,
                        text = view.id ?: "unknown id",
                        style = TextStyle(color = color, fontSize = 10.sp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewDLTSimplifiedInfoView() {
    val dltMessage = LogMessage(SampleData.getSampleDltMessages(1)[0])
    DLTSimplifiedInfoView(Modifier.width(200.dp), logMessage = dltMessage, 0)
}

@Preview
@Composable
fun PreviewDLTSimplifiedInfoView2() {
    val standardHeader = StandardHeader(
        headerType = HeaderType(
            64.toByte(),
            useExtendedHeader = false,
            payloadBigEndian = true,
            withEcuId = false,
            withSessionId = false,
            withTimestamp = false,
            versionNumber = 1,
        ),
        1.toUByte(),
        10.toUShort()
    )
    val payload =
        "TestView[2797]: onGlobalFocusChanged: oldFocus:com.ui.custom.ProgressBarFrameLayout{f5e8f76 VFE...CL. ......ID 2298,22-2835,709 #7f090453 app:id/theme_container aid=1073741849}, newFocus:com.android.car.ui.FocusParkingView{736743f VFED..... .F...... 0,0-1,1 #7f090194 app:id/focus_parking_view aid=1073741832}"
    val dltMessage = DLTMessage(
        1L,
        "ECU",
        standardHeader = standardHeader,
        extendedHeader = null,
        payload = payload,
        sizeBytes = 100,
    )
    DLTSimplifiedInfoView(
        modifier = Modifier.height(400.dp).fillMaxWidth(), logMessage = LogMessage(dltMessage), messageIndex = 0
    )
}