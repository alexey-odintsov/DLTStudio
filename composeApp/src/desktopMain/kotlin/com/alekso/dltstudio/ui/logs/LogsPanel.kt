package com.alekso.dltstudio.ui.logs

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alekso.dltparser.dlt.SampleData
import com.alekso.dltstudio.ui.HorizontalDivider
import com.alekso.dltstudio.ui.ParseSession
import java.io.File

@Composable
fun LogsPanel(
    modifier: Modifier = Modifier,
    dltSession: ParseSession?,
    colorFilters: List<CellColorFilter> = emptyList(),
) {
    var selectedRow by remember { mutableStateOf(0) }

    Row(modifier = modifier.fillMaxWidth()) {
        LazyScrollable(
            Modifier.weight(1f).fillMaxHeight().background(Color.LightGray),
            dltSession,
            colorFilters,
            selectedRow = selectedRow,
        ) { i -> selectedRow = i }
        HorizontalDivider()
        LogPreview(
            Modifier.fillMaxHeight().width(300.dp),
            dltSession?.dltMessages?.getOrNull(selectedRow)
        )
    }
}

@Preview
@Composable
fun PreviewLogsPanel() {
    val dltSession = ParseSession({}, File(""))
    dltSession.dltMessages.addAll(SampleData.getSampleDltMessages(20))

    Box(modifier = Modifier.width(400.dp).height(500.dp).background(Color.Yellow)) {
        LogsPanel(
            Modifier.fillMaxSize(),
            dltSession = dltSession,
        )
    }
}