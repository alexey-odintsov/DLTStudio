package com.alekso.dltstudio.logs.infopanel

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltstudio.TimeFormatter
import com.alekso.dltstudio.logs.insights.LogInsight
import com.alekso.dltstudio.model.LogMessage
import com.alekso.dltstudio.ui.CustomButton
import com.alekso.dltstudio.ui.CustomEditText

@Composable
fun DLTSimplifiedInfoView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    messageIndex: Int,
    insights: SnapshotStateList<LogInsight>? = null,
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

                insights?.forEachIndexed { index, insight ->
                    Header(
                        modifier = paddingModifier.padding(top = 8.dp),
                        text = "Insight $index:"
                    )
                    TableRow(
                        0,
                        "",
                        insight.text, // todo: To use AnnotatedString.fromHtml in compose 1.7.0-alpha07 and later
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