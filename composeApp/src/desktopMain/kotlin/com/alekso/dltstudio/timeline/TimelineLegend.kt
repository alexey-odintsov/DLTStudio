package com.alekso.dltstudio.timeline

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.colors.ColorPalette

@Composable
fun TimelineLegend(
    modifier: Modifier,
    title: String,
    entries: TimelineEntries? = null,
    updateHighlightedKey: (String?) -> Unit,
    highlightedKey: String? = null,
) {
    val state = rememberLazyListState()
    val map = entries?.getEntriesMap()

    Box(modifier = modifier.padding(start = 4.dp, end = 4.dp)) {
        Column(Modifier.fillMaxSize()) {
            Text(
                title,
                fontWeight = FontWeight(600),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            if (!map.isNullOrEmpty()) {
                LazyColumn(Modifier, state) {
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
                                modifier = Modifier.weight(1f),
                                text = key,
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