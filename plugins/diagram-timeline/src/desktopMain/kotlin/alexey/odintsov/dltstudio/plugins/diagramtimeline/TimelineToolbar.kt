package alexey.odintsov.dltstudio.plugins.diagramtimeline

import alexey.odintsov.dltstudio.plugins.diagramtimeline.db.RecentTimelineFilterFileEntry
import alexey.odintsov.dltstudio.plugins.diagramtimeline.filters.AnalyzeState
import alexey.odintsov.uicomponents.Tooltip
import alexey.odintsov.uicomponents.buttons.CustomButton
import alexey.odintsov.uicomponents.buttons.CustomDropDownButton
import alexey.odintsov.uicomponents.buttons.DropDownItem
import alexey.odintsov.uicomponents.buttons.ImageButton
import alexey.odintsov.uicomponents.preview.PreviewDarkAndLightTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dltstudio.resources.Res
import dltstudio.resources.icon_color_filters
import dltstudio.resources.icon_fit
import dltstudio.resources.icon_left
import dltstudio.resources.icon_right
import dltstudio.resources.icon_run
import dltstudio.resources.icon_stop
import dltstudio.resources.icon_zoom_in
import dltstudio.resources.icon_zoom_out

sealed interface ToolbarAction {
    object AnalyzeClicked : ToolbarAction
    object TimelineFiltersClicked : ToolbarAction
    object LoadFilterClicked : ToolbarAction
    object SaveFilterClicked : ToolbarAction
    object SaveFilterAsClicked : ToolbarAction
    object ClearFilterClicked : ToolbarAction
    object LeftClicked : ToolbarAction
    object RightClicked : ToolbarAction
    object ZoomInClicked : ToolbarAction
    object ZoomOutClicked : ToolbarAction
    object ZoomFitClicked : ToolbarAction
    data class RecentFilterClicked(val path: String) : ToolbarAction
    data class DragTimeline(val dx: Float) : ToolbarAction
}

@Composable
fun TimelineToolbar(
    analyzeState: AnalyzeState,
    onAction: (ToolbarAction) -> Unit,
    recentFiltersFiles: List<RecentTimelineFilterFileEntry>,
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
                onClick = { onAction(ToolbarAction.AnalyzeClicked) },
                tintable = false
            )
        }
        VerticalDivider()

        Tooltip(text = "Move offset to the left") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_left,
                title = "Move left",
                onClick = { onAction(ToolbarAction.LeftClicked) }
            )
        }
        Tooltip(text = "Move offset to the right") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_right,
                title = "Move right",
                onClick = { onAction(ToolbarAction.RightClicked) }
            )
        }

        Tooltip(text = "Zoom in") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_zoom_in,
                title = "Zoom in",
                onClick = { onAction(ToolbarAction.ZoomInClicked) }
            )
        }
        Tooltip(text = "Zoom out") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_zoom_out,
                title = "Zoom out",
                onClick = { onAction(ToolbarAction.ZoomOutClicked) }
            )
        }
        Tooltip(text = "Fit timeline") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_fit,
                title = "Fit timeline",
                onClick = { onAction(ToolbarAction.ZoomFitClicked) },
            )
        }

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))
        Tooltip(text = "Manage timeline filters") {
            ImageButton(
                modifier = Modifier.size(32.dp),
                icon = Res.drawable.icon_color_filters,
                title = "Timeline filters",
                onClick = { onAction(ToolbarAction.TimelineFiltersClicked) },
                tintable = false,
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
                    modifier = Modifier.wrapContentSize().background(MaterialTheme.colorScheme.background)
                ) {
                    recentFiltersFiles.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = s.fileName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            onClick = {
                                expanded = false
                                onAction(ToolbarAction.RecentFilterClicked(recentFiltersFiles[index].path))
                            }
                        )
                    }
                }
            }
        }

        CustomButton(
            modifier = Modifier, onClick = { onAction(ToolbarAction.LoadFilterClicked) },
        ) {
            Text("Load")
        }

        CustomDropDownButton(
            items = remember {
                listOf(
                    DropDownItem("Save", { onAction(ToolbarAction.SaveFilterClicked) }),
                    DropDownItem("Save As", { onAction(ToolbarAction.SaveFilterAsClicked) }),
                )
            }
        )

        CustomButton(
            modifier = Modifier, onClick = { onAction(ToolbarAction.ClearFilterClicked) },
        ) {
            Text("Clear")
        }
    }
}


@Preview
@Composable
private fun PreviewTimelineToolbar() {
    PreviewDarkAndLightTheme(true) {
        TimelineToolbar(
            analyzeState = AnalyzeState.ANALYZING,
            onAction = {},
            recentFiltersFiles = mutableStateListOf(
                RecentTimelineFilterFileEntry("timeline-filter.txt", "/path/to/file/"),
                RecentTimelineFilterFileEntry("timeline-filter2.txt", "/path/to/file/"),
            ),
            currentFilterFile = null
        )
        HorizontalDivider(Modifier.fillMaxWidth().height(1.dp))
    }
}