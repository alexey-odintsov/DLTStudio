package com.alekso.dltstudio.plugins.loginfoview

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.model.contract.LogMessage
import com.alekso.dltstudio.theme.SystemTheme
import com.alekso.dltstudio.theme.ThemeManager
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.CustomEditText
import com.alekso.dltstudio.uicomponents.ImageButton
import dltstudio.resources.Res
import dltstudio.resources.icon_copy

@Composable
fun LogInfoView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    onCommentUpdated: (LogMessage, String?) -> Unit = { _, _ -> },
) {
    if (logMessage == null) return

    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)
    var comment by remember(key1 = logMessage) { mutableStateOf(logMessage.comment) }

    Column(modifier = modifier.fillMaxHeight()) {
        logMessage.dltMessage.let {

            Box(Modifier.weight(1f)) {
                val scrollState = rememberScrollState()
                Column(Modifier.verticalScroll(scrollState)) {
                    Header(
                        modifier = paddingModifier, text = "Message #${logMessage.id}:"
                    )
                    TextSection(
                        "${LocalFormatter.current.formatDateTime(it.timeStampUs)} " + "${it.standardHeader.ecuId} " + "${it.extendedHeader?.applicationId} " + "${it.extendedHeader?.contextId} "
                    )
                    val payload = it.payloadText()
                    Header(
                        modifier = paddingModifier.padding(top = 8.dp),
                        text = "Message:",
                        textToCopy = payload,
                        showCopy = true
                    )
                    TextSection(text = payload)
                    Header(
                        modifier = paddingModifier.padding(top = 8.dp), text = "Comment:"
                    )
                    Row {
                        CustomEditText(
                            modifier = Modifier.fillMaxWidth().height(64.dp).padding(top = 4.dp)
                                .align(Alignment.Top),
                            singleLine = false,
                            value = comment ?: "",
                            minLines = 3,
                            maxLines = 3,
                            onValueChange = {
                                comment = it
                            })
                    }
                    CustomButton(
                        modifier = Modifier.padding(start = 6.dp), onClick = {
                            onCommentUpdated(
                                logMessage,
                                if (comment.isNullOrEmpty()) null else comment
                            )
                        }) {
                        Text(text = if (logMessage.comment != null) "Update" else "Add")
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = scrollState
                    )
                )

            }
        }
    }
}

@Composable
fun TextSection(text: String) {
    SelectionContainer {
        Text(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            text = text
        )
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    text: String,
    textToCopy: String? = null,
    showCopy: Boolean = false
) {
    Row {
        Text(
            modifier = modifier.padding(top = 4.dp).weight(1f),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight(600),
            fontSize = 11.sp,
            text = text
        )
        if (showCopy && textToCopy != null) {
            val clipboardManager = LocalClipboardManager.current
            ImageButton(
                modifier = Modifier.padding(0.dp).size(28.dp),
                icon = Res.drawable.icon_copy,
                title = "Copy text",
                onClick = {
                    clipboardManager.setText(AnnotatedString(textToCopy))
                })
        }
    }
    HorizontalDivider()
}


@Preview
@Composable
fun PreviewLogSimplifiedInfoView() {
    ThemeManager.CustomTheme(SystemTheme(false)) {
        val dltMessage = SampleData.create(
            payloadText = "TestView[2797]: onGlobalFocusChanged: oldFocus:com.ui.custom.ProgressBarFrameLayout{f5e8f76 VFE...CL. ......ID 2298,22-2835,709 #7f090453 app:id/theme_container aid=1073741849}, newFocus:com.android.car.ui.FocusParkingView{736743f VFED..... .F...... 0,0-1,1 #7f090194 app:id/focus_parking_view aid=1073741832} {bounds:Rect(0, 0 - 342, 240),hasBoundsTransaction,}",
        )
        LogInfoView(
            modifier = Modifier.height(250.dp).fillMaxWidth(),
            logMessage = LogMessage(dltMessage),
        )
    }
}