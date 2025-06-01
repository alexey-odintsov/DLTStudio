package com.alekso.dltstudio.plugins.diagramtimeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alekso.dltstudio.plugins.diagramtimeline.db.RecentTimelineFilterFileEntry
import com.alekso.dltstudio.plugins.diagramtimeline.filters.AnalyzeState
import com.alekso.dltstudio.uicomponents.CustomButton
import com.alekso.dltstudio.uicomponents.HorizontalDivider
import com.alekso.dltstudio.uicomponents.ImageButton
import com.alekso.dltstudio.uicomponents.Tooltip
import dltstudio.resources.Res
import dltstudio.resources.icon_color_filters
import dltstudio.resources.icon_fit
import dltstudio.resources.icon_left
import dltstudio.resources.icon_right
import dltstudio.resources.icon_run
import dltstudio.resources.icon_stop
import dltstudio.resources.icon_zoom_in
import dltstudio.resources.icon_zoom_out

interface ToolbarCallbacks {
    fun onAnalyzeClicked()
    fun onTimelineFiltersClicked()
    fun onLoadFilterClicked()
    fun onSaveFilterClicked()
    fun onClearFilterClicked()
    fun onRecentFilterClicked(path: String)
    fun onLeftClicked()
    fun onRightClicked()
    fun onZoomInClicked()
    fun onZoomOutClicked()
    fun onZoomFitClicked()
    fun onDragTimeline(dx: Float)


    object Stub : ToolbarCallbacks {
        override fun onAnalyzeClicked() = Unit
        override fun onTimelineFiltersClicked() = Unit
        override fun onLoadFilterClicked() = Unit
        override fun onSaveFilterClicked() = Unit
        override fun onClearFilterClicked() = Unit
        override fun onRecentFilterClicked(path: String) = Unit
        override fun onLeftClicked() = Unit
        override fun onRightClicked() = Unit
        override fun onZoomInClicked() = Unit
        override fun onZoomOutClicked() = Unit
        override fun onZoomFitClicked() = Unit
        override fun onDragTimeline(dx: Float) = Unit
    }
}


@Composable
fun TimelineToolbar(
    analyzeState: AnalyzeState,
    callbacks: ToolbarCallbacks,
    recentFiltersFiles: SnapshotStateList<RecentTimelineFilterFileEntry>,
    currentFilterFile: RecentTimelineFilterFileEntry?,
) {

    Row(
        Modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Tooltip(text = "Start/Stop timeline analyzing") {
            ImageButton(
                modifier = Modifier.padding(start = 8.dp).size(32.dp),
                icon = if (analyzeState == AnalyzeState.IDLE) {
                    Res.drawable.icon_run
                } else {
                    Res.drawable.icon_stop
                },
                title = "Analyze timeline",
                onClick = callbacks::onAnalyzeClicked
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Tooltip(text = "Move offset to the left") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_left,
                title = "Move left",
                onClick = callbacks::onLeftClicked
            )
        }
        Tooltip(text = "Move offset to the right") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_right,
                title = "Move right",
                onClick = callbacks::onRightClicked
            )
        }

        Tooltip(text = "Zoom in") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_zoom_in,
                title = "Zoom in",
                onClick = callbacks::onZoomInClicked
            )
        }
        Tooltip(text = "Zoom out") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_zoom_out,
                title = "Zoom out",
                onClick = callbacks::onZoomOutClicked
            )
        }
        Tooltip(text = "Fit timeline") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_fit,
                title = "Fit timeline",
                onClick = callbacks::onZoomFitClicked
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))
        Tooltip(text = "Manage timeline filters") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_color_filters,
                title = "Timeline filters",
                onClick = callbacks::onTimelineFiltersClicked
            )
        }

        if (recentFiltersFiles.isNotEmpty()) {
            var expanded by remember { mutableStateOf(false) }
            Box {
                Text(
                    currentFilterFile?.fileName ?: "No filters file selected",
                    modifier = Modifier.padding(horizontal = 4.dp)
                        .clickable(onClick = { expanded = true })
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(200.dp)
                ) {
                    recentFiltersFiles.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            callbacks.onRecentFilterClicked(recentFiltersFiles[index].path)
                        }) {
                            Text(text = s.fileName, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        CustomButton(
            modifier = Modifier.heightIn(max = 24.dp), onClick = callbacks::onLoadFilterClicked
        ) {
            Text("Load")
        }

        CustomButton(
            modifier = Modifier.heightIn(max = 24.dp), onClick = callbacks::onSaveFilterClicked
        ) {
            Text("Save")
        }

        CustomButton(
            modifier = Modifier.heightIn(max = 24.dp), onClick = callbacks::onClearFilterClicked
        ) {
            Text("Clear")
        }
    }
}

@Preview
@Composable
fun PreviewTimelineToolbar() {
    Column {
        TimelineToolbar(
            analyzeState = AnalyzeState.ANALYZING,
            callbacks = ToolbarCallbacks.Stub,
            recentFiltersFiles = mutableStateListOf(
                RecentTimelineFilterFileEntry("timeline-filter.txt", "/path/to/file/"),
                RecentTimelineFilterFileEntry("timeline-filter2.txt", "/path/to/file/"),
            ),
            currentFilterFile = null
        )
        Divider()
    }
}