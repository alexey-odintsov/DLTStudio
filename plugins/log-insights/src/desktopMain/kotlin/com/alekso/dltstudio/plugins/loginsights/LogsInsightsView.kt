package com.alekso.dltstudio.plugins.loginsights


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alekso.dltmessage.SampleData
import com.alekso.dltstudio.model.contract.LogMessage

@Composable
fun LogInsightsView(
    modifier: Modifier = Modifier,
    logMessage: LogMessage?,
    insights: SnapshotStateList<LogInsight>? = null,
) {
    if (logMessage == null) return

    val paddingModifier = Modifier.padding(start = 4.dp, end = 4.dp)

    SelectionContainer {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            logMessage.dltMessage.let {
                Header(
                    modifier = paddingModifier,
                    text = "DLT Message #${logMessage.id}:"
                )

                if (!insights.isNullOrEmpty()) {
                    insights.forEachIndexed { index, insight ->
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
                } else {
                    Text(modifier = Modifier.padding(4.dp), text = "No insights found")
                }
            }
        }
    }
}

@Composable
fun TableRow(col1Width: Int, col1Value: String, col2Value: String?) {
    val cellBackground = Color(250, 250, 250)

    Row(modifier = Modifier.background(Color.LightGray)) {
        if (col1Width != 0) {
            Box(
                modifier = Modifier.padding(end = 1.dp, bottom = 1.dp)
                    .background(color = cellBackground)
            ) {
                MonoText(
                    modifier = Modifier.width(col1Width.dp).padding(start = 2.dp, end = 2.dp),
                    text = col1Value
                )
            }
        }
        Box(
            modifier = Modifier.weight(1f).padding(end = 1.dp, bottom = 1.dp)
                .background(color = cellBackground)
        ) {
            MonoText(
                modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                text = col2Value ?: ""
            )
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier.padding(top = 4.dp),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight(600),
        fontSize = 11.sp,
        text = text
    )
    Divider()
}

@Composable
fun MonoText(modifier: Modifier = Modifier, text: String) {
    Text(
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        text = text
    )
}

@Preview
@Composable
fun PreviewLogInsightsView() {
    LogInsightsView(
        Modifier,
        LogMessage(SampleData.create(payloadText = "Choreographer[4476]: Skipped 36 frames!  The application may be doing too much work on its main thread.")),
        mutableStateListOf(
            LogInsight("Skipped frames", "The app skipped 55 frames!")
        )
    )
}

@Preview
@Composable
fun PreviewLogInsightsViewNoInsights() {
    LogInsightsView(
        Modifier,
        LogMessage(SampleData.create(payloadText = "Choreographer[4476]: Skipped 36 frames!  The application may be doing too much work on its main thread.")),
        mutableStateListOf()
    )
}
