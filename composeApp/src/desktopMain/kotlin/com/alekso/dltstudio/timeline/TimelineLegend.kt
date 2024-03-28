package com.alekso.dltstudio.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.colors.ColorPalette
import java.time.Instant

@Composable
fun TimelineLegend(
    modifier: Modifier,
    title: String,
    entries: TimeLineEntries<*>? = null,
    updateHighlightedKey: (String?) -> Unit,
    highlightedKey: String? = null,
) {
    val state = rememberLazyListState()
    val map = entries?.map

    Box(modifier = modifier.padding(start = 4.dp, end = 4.dp)) {
        Column(Modifier.fillMaxSize()) {
            Text(
                title,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(bottom = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!map.isNullOrEmpty()) {
                val horizontalState = rememberScrollState()
                LazyColumn(Modifier.horizontalScroll(horizontalState).wrapContentWidth(Alignment.Start), state) {
                    val keys = map.keys.toList()

                    items(keys.size) { i ->
                        val key = keys[i]
                        Row(
                            Modifier.selectable(
                                selected = highlightedKey == key,
                                onClick = { updateHighlightedKey(if (highlightedKey != key) key else null) })
                        ) {
                            Box(
                                modifier = Modifier.width(30.dp).height(6.dp).padding(end = 4.dp)
                                    .align(Alignment.CenterVertically)
                                    .background(ColorPalette.getColor(i))
                            )
                            Text(
                                modifier = Modifier,
                                text = key,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                fontWeight = FontWeight(if (highlightedKey == key) 600 else 400)
                            )
                        }
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}

@Preview
@Composable
fun PreviewTimeLineLegend() {
    val ts = Instant.now().toEpochMilli() * 1000L

    val entries = TimeLineEventEntries()
    entries.addEntry(
        TimeLineEventEntry(
            ts,
            "my test app long long name (pid: 99982)",
            TimeLineEvent("CRASH", null)
        )
    )
    entries.addEntry(
        TimeLineEventEntry(
            ts,
            "Second app (pid: 23455)",
            TimeLineEvent("ANR", null)
        )
    )
    entries.addEntry(
        TimeLineEventEntry(
            ts,
            "System app (pid: 0)",
            TimeLineEvent("WTF", null)
        )
    )


    Column(Modifier.background(Color.Gray)) {
        TimelineLegend(
            modifier = Modifier.width(200.dp),
            title = "Very long test title with more than one line text",
            entries = entries,
            updateHighlightedKey = {},
            highlightedKey = null
        )
    }
}